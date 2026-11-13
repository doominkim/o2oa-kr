package com.x.organization.assemble.control.jaxrs.personcard;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.x.base.core.container.EntityManagerContainer;
import com.x.base.core.container.factory.EntityManagerContainerFactory;
import com.x.base.core.project.http.ActionResult;
import com.x.base.core.project.http.EffectivePerson;
import com.x.base.core.project.jaxrs.WoFile;
import com.x.base.core.project.logger.Logger;
import com.x.base.core.project.logger.LoggerFactory;
import com.x.organization.core.entity.PersonCard;

class ActionCreateCode extends BaseAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActionCreateCode.class);

    // 개인 주소록 QR 코드 생성.
    ActionResult<Wo> qrcode(EffectivePerson effectivePerson, String flag) throws Exception {

        LOGGER.debug("execute:{}, flag:{}.", effectivePerson::getDistinguishedName, () -> flag);

        try (EntityManagerContainer emc = EntityManagerContainerFactory.instance().create()) {
            ActionResult<Wo> result = new ActionResult<>();
            PersonCard personCard = emc.find(flag, PersonCard.class);
            if (null == personCard) {
                throw new ExceptionPersonCardNotExist(flag);
            }

            String fname = personCard.getId() + ".png";
            String content = "BEGIN:VCARD\n" + "VERSION:3.0\n" + "N:" + personCard.getName() + "\n";

            if (!personCard.getOfficePhone().equals("")) {
                content = content + "TEL:" + personCard.getOfficePhone() + "\n";
            }
            if (!personCard.getMobile().equals("")) {
                content = content + "TEL;CELL:" + personCard.getMobile() + "\n";
            }
            if (!personCard.getAddress().equals("")) {
                content = content + "ADR;HOME;POSTAL:;;" + personCard.getAddress() + ";;;;\n";
            }
            if (!personCard.getGroupType().equals("")) {
                content = content + "ORG:" + personCard.getGroupType() + "\n";
                // content = content+"TITLE:"+personCard.getGroupType()+"\n";
            }
            if (!personCard.getDescription().equals("")) {
                content = content + "NOTE:" + personCard.getDescription() + "\n";
            }
            content = content + "END:VCARD";

            /*
             * content = "BEGIN:VCARD\n" + "VERSION:1.0\n" + "N:홍길동\n" +
             * "EMAIL:1606841559@qq.com\n" + "TEL:12345678912\n" + "TEL;CELL:12345678913\n"
             * + "ADR;HOME;POSTAL:;;테헤란로 391;강남구;서울특별시;433330;대한민국\n" + "ORG:서울\n" +
             * "TITLE:소프트웨어 엔지니어\n" + "URL:http://blog.csdn.net/lidew521\n" + "NOTE:테스트입니다...\n"
             * + "END:VCARD";
             */
            // getBarCode(content,path);

            Wo wo = new Wo(getBarCodeWo(content), this.contentType(false, fname),
                    this.contentDisposition(false, fname));
            result.setData(wo);
            return result;
        }
    }

    /**
     * QR 코드 구현
     * 
     * @param msg  /QR 코드에 포함될 정보
     * @param path /QR 코드 저장 경로
     */
    public static void getBarCode(String msg, String path) {
        File file = new File(path);
        try (OutputStream ous = new FileOutputStream(file)) {
            if (StringUtils.isEmpty(msg)) {
                return;
            }
            String format = "png";
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            Map<EncodeHintType, String> map = new HashMap<EncodeHintType, String>();
            // 인코딩 설정, EncodeHintType 클래스에서 MAX_SIZE,
            // ERROR_CORRECTION, CHARACTER_SET, DATA_MATRIX_SHAPE, AZTEC_LAYERS 등 매개변수 설정 가능
            map.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            map.put(EncodeHintType.MARGIN, "1");
            // QR 코드 생성
            BitMatrix bitMatrix = new MultiFormatWriter().encode(msg, BarcodeFormat.QR_CODE, 300, 300, map);
            MatrixToImageWriter.writeToStream(bitMatrix, format, ous);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * QR 코드 구현
     * 
     * @param msg /QR 코드에 포함될 정보
     */
    public static byte[] getBarCodeWo(String msg) {
        byte[] bs = null;
        try {
            String format = "png";
            Map<EncodeHintType, String> map = new HashMap<EncodeHintType, String>();
            // 인코딩 설정, EncodeHintType 클래스에서 MAX_SIZE,
            // ERROR_CORRECTION, CHARACTER_SET, DATA_MATRIX_SHAPE, AZTEC_LAYERS 등 매개변수 설정 가능
            map.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            map.put(EncodeHintType.MARGIN, "1");
            // QR 코드 생성
            BitMatrix bitMatrix = new MultiFormatWriter().encode(msg, BarcodeFormat.QR_CODE, 300, 300, map);
            BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "gif", out);
            bs = out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return bs;
    }

    public static class Wo extends WoFile {

        public Wo(byte[] bytes, String contentType, String contentDisposition) {
            super(bytes, contentType, contentDisposition);
        }

    }

}
