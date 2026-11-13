package com.x.attendance.assemble.control.jaxrs.v2.leavemanager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

import com.x.attendance.assemble.control.Business;
import com.x.attendance.assemble.control.ThisApplication;
import com.x.attendance.assemble.control.jaxrs.v2.AttendanceV2Helper;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionEmptyParameter;
import com.x.attendance.assemble.control.jaxrs.v2.ExceptionWithMessage;
import com.x.attendance.assemble.control.jaxrs.v2.leavemanager.model.AttendanceV2LeaveManager;
import com.x.attendance.entity.v2.AttendanceV2LeaveLedger;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.entity.annotation.CheckPersistType;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.config.StorageMapping;
import com.x.base.core.project.exception.ExceptionAccessDenied;
import com.x.base.core.project.gson.GsonPropertyObject;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.base.core.project.organization.Person;
import com.x.base.core.project.tools.DateTools;
import com.x.general.core.entity.GeneralFile;

public class ActionLeaveLedgerImportExcel extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionLeaveLedgerImportExcel.class);

    private static final ReentrantLock LOCK = new ReentrantLock();

    private static final int ERROR_COLUMN_INDEX = 3;

    ActionResult<Wo> execute(EffectivePerson effectivePerson, String leaveTypeId, String grantPeriod, byte[] bytes,
            FormDataContentDisposition disposition) throws Exception {
        if (bytes == null || bytes.length == 0) {
            throw new ExceptionEmptyParameter("Excel 파일");
        }
        if (StringUtils.isBlank(leaveTypeId)) {
            throw new ExceptionEmptyParameter("휴가 유형 ID");
        }
        if (StringUtils.isBlank(grantPeriod)) {
            throw new ExceptionEmptyParameter("지급 주기");
        }
        LOCK.lock();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("휴가 지급 배치 데이터 가져오기 시작.");
        }
        try (InputStream is = new ByteArrayInputStream(bytes);
                XSSFWorkbook workbook = new XSSFWorkbook(is);
                ByteArrayOutputStream os = new ByteArrayOutputStream();
                EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            Business business = new Business(emc);
            if (!business.isManager(effectivePerson)) {
                throw new ExceptionAccessDenied(effectivePerson);
            }

            Sheet sheet = workbook.getSheetAt(0);
            int firstRow = sheet.getFirstRowNum() + 1;
            int lastRow = sheet.getLastRowNum();
            int errorRowNumber = 0;
            int successRowNumber = 0;
            Date now = new Date();
            for (int i = firstRow; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null || isBlankRow(row)) {
                    continue;
                }
                try {
                    AttendanceV2LeaveLedger ledger = buildLedgerFromRow(business, row, leaveTypeId, grantPeriod, now);
                    ActionLeaveLedgerPost.validateLedgerNotExists(emc.listEqualAndEqualAndEqual(
                            AttendanceV2LeaveLedger.class, AttendanceV2LeaveLedger.grantPeriod_FIELDNAME,
                            ledger.getGrantPeriod(), AttendanceV2LeaveLedger.leaveTypeId_FIELDNAME,
                            ledger.getLeaveTypeId(), AttendanceV2LeaveLedger.person_FIELDNAME, ledger.getPerson()));

                    emc.beginTransaction(AttendanceV2LeaveLedger.class);
                    emc.persist(ledger, CheckPersistType.all);
                    emc.commit();

                    ActionLeaveLedgerPost.addLeaveTransaction(emc, ledger);
                    AttendanceV2LeaveManager.asyncUpdateLeaveAccount(ledger.getPerson(), ledger.getLeaveTypeId());
                    successRowNumber++;
                } catch (Exception e) {
                    AttendanceV2Helper.setExcelCellError(row, e.getLocalizedMessage(), ERROR_COLUMN_INDEX);
                    errorRowNumber++;
                }
            }

            String name = "attendance_leave_ledger_data_input_"
                    + DateTools.format(new Date(), DateTools.formatCompact_yyyyMMddHHmmss) + ".xlsx";
            workbook.write(os);
            String flag = saveAttachment(os.toByteArray(), name, effectivePerson);

            ActionResult<Wo> result = new ActionResult<>();
            Wo wo = new Wo();
            wo.setFlag(flag);
            wo.setErrorRows(errorRowNumber);
            wo.setSuccessRows(successRowNumber);
            result.setData(wo);
            return result;
        } finally {
            LOCK.unlock();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("휴가 지급 배치 데이터 가져오기 종료.");
            }
        }
    }

    static AttendanceV2LeaveLedger buildLedgerFromRow(Business business, Row row, String leaveTypeId,
            String grantPeriod, Date now) throws Exception {
        String person = AttendanceV2Helper.getExcelCellStringValue(row.getCell(0));
        if (StringUtils.isBlank(person)) {
            throw new ExceptionEmptyParameter("사용자");
        }
        Person mPerson = business.organization().person().getObject(person, true);
        if (mPerson == null) {
            throw new ExceptionWithMessage("사용자 식별자에 해당하는 인원을 찾을 수 없습니다");
        }

        String grantAmountString = AttendanceV2Helper.getExcelCellStringValue(row.getCell(1));
        if (StringUtils.isBlank(grantAmountString)) {
            throw new ExceptionEmptyParameter("지급 한도");
        }
        Double grantAmount = parseGrantAmount(grantAmountString);

        String expireTimeString = AttendanceV2Helper.getExcelCellDateFormat(row.getCell(2), DateTools.format_yyyyMMdd);
        if (StringUtils.isBlank(expireTimeString)) {
            throw new ExceptionEmptyParameter("만료 날짜");
        }
        Date expireTime = parseExpireTime(expireTimeString);

        ActionLeaveLedgerPost.Wi wi = new ActionLeaveLedgerPost.Wi();
        wi.setPerson(mPerson.getDistinguishedName());
        wi.setLeaveTypeId(leaveTypeId);
        wi.setGrantPeriod(grantPeriod);
        wi.setGrantAmount(grantAmount);
        wi.setExpireTime(expireTime);
        return ActionLeaveLedgerPost.buildLedger(wi, now, true);
    }

    static Double parseGrantAmount(String grantAmountString) throws Exception {
        try {
            Double grantAmount = Double.parseDouble(grantAmountString);
            if (grantAmount.isNaN() || grantAmount.isInfinite()) {
                throw new IllegalArgumentException();
            }
            return grantAmount;
        } catch (Exception e) {
            throw new ExceptionWithMessage("지급 한도 형식이 올바르지 않습니다");
        }
    }

    static Date parseExpireTime(String expireTimeString) throws Exception {
        try {
            return DateTools.parse(expireTimeString, DateTools.format_yyyyMMdd);
        } catch (Exception e) {
            throw new ExceptionWithMessage("만료 날짜 형식이 올바르지 않습니다");
        }
    }

    private boolean isBlankRow(Row row) {
        return StringUtils.isBlank(AttendanceV2Helper.getExcelCellStringValue(row.getCell(0)))
                && StringUtils.isBlank(AttendanceV2Helper.getExcelCellStringValue(row.getCell(1)))
                && StringUtils.isBlank(AttendanceV2Helper.getExcelCellStringValue(row.getCell(2)));
    }

    private String saveAttachment(byte[] bytes, String attachmentName, EffectivePerson effectivePerson)
            throws Exception {
        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            StorageMapping gfMapping = ThisApplication.context().storageMappings().random(GeneralFile.class);
            GeneralFile generalFile = new GeneralFile(gfMapping.getName(), attachmentName,
                    effectivePerson.getDistinguishedName());
            generalFile.saveContent(gfMapping, bytes, attachmentName);
            emc.beginTransaction(GeneralFile.class);
            emc.persist(generalFile, CheckPersistType.all);
            emc.commit();
            return generalFile.getId();
        }
    }

    public static class Wo extends GsonPropertyObject {

        private static final long serialVersionUID = 8076084539055540316L;

        @FieldDescribe("반환 결과 식별자, 결과 파일 다운로드에 사용")
        private String flag;

        @FieldDescribe("오류 데이터 건수")
        private int errorRows;

        @FieldDescribe("가져오기 성공 데이터 건수")
        private int successRows;

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }

        public int getErrorRows() {
            return errorRows;
        }

        public void setErrorRows(int errorRows) {
            this.errorRows = errorRows;
        }

        public int getSuccessRows() {
            return successRows;
        }

        public void setSuccessRows(int successRows) {
            this.successRows = successRows;
        }
    }
}
