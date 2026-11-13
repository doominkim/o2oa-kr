package com.x.query.core.entity.neural;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.OrderColumn;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.openjpa.persistence.PersistentCollection;
import org.apache.openjpa.persistence.PersistentMap;
import org.apache.openjpa.persistence.jdbc.ContainerTable;
import org.apache.openjpa.persistence.jdbc.ElementColumn;
import org.apache.openjpa.persistence.jdbc.ElementIndex;
import org.apache.openjpa.persistence.jdbc.Index;
import org.apache.openjpa.persistence.jdbc.KeyColumn;
import org.apache.openjpa.persistence.jdbc.KeyIndex;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.input.WeightedSum;
import org.neuroph.core.transfer.Linear;
import org.neuroph.core.transfer.Sigmoid;
import org.neuroph.nnet.comp.neuron.BiasNeuron;
import org.neuroph.nnet.comp.neuron.InputNeuron;
import org.neuroph.nnet.comp.neuron.InputOutputNeuron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.ConnectionFactory;
import org.neuroph.util.LayerFactory;
import org.neuroph.util.NeuralNetworkFactory;
import org.neuroph.util.NeuralNetworkType;
import org.neuroph.util.NeuronProperties;

import com.x.base.core.entity.JpaObject;
import com.x.base.core.entity.SliceJpaObject;
import com.x.base.core.entity.annotation.CheckPersist;
import com.x.base.core.entity.annotation.CheckRemove;
import com.x.base.core.entity.annotation.CitationNotExist;
import com.x.base.core.entity.annotation.ContainerEntity;
import com.x.base.core.entity.annotation.Flag;
import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.tools.MapTools;
import com.x.query.core.entity.PersistenceProperties;

import io.swagger.v3.oas.annotations.media.Schema;
@Schema(name = "Model", description = "데이터 센터 신경망 모델.")
@Entity
@ContainerEntity(dumpSize = 1000, type = ContainerEntity.Type.content, reference = ContainerEntity.Reference.strong)
@Table(name = PersistenceProperties.Neural.Model.table, uniqueConstraints = {
		@UniqueConstraint(name = PersistenceProperties.Neural.Model.table + JpaObject.IndexNameMiddle
				+ JpaObject.DefaultUniqueConstraintSuffix, columnNames = { JpaObject.IDCOLUMN,
						JpaObject.CREATETIMECOLUMN, JpaObject.UPDATETIMECOLUMN, JpaObject.SEQUENCECOLUMN }) })
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class Model extends SliceJpaObject {

	private static final long serialVersionUID = -5610293696763235753L;

	private static final String TABLE = PersistenceProperties.Neural.Model.table;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@FieldDescribe("데이터베이스 기본 키, 자동 생성.")
	@Id
	@Column(length = length_id, name = ColumnNamePrefix + id_FIELDNAME)
	@CheckRemove(citationNotExists = { @CitationNotExist(type = Entry.class, fields = Entry.model_FIELDNAME),
			@CitationNotExist(type = InText.class, fields = InText.model_FIELDNAME),
			@CitationNotExist(type = OutText.class, fields = OutText.model_FIELDNAME),
			@CitationNotExist(type = InValue.class, fields = InValue.model_FIELDNAME),
			@CitationNotExist(type = OutValue.class, fields = OutValue.model_FIELDNAME) })
	private String id = createId();

	/* 위는 JpaObject 기본 필드 */

	public void onPersist() throws Exception {

	}

	/* 실행 메서드 업데이트 */

	public static final String PROPERTY_MLP_HIDDENLAYERCOUNT = "hiddenLayerCount";
	public static final Integer DEFAULT_MLP_HIDDENLAYERCOUNT = 2;

	public static final String PROPERTY_MLP_MAXERROR = "maxError";
	public static final Double DEFAULT_MLP_MAXERROR = 0.01;

	public static final String PROPERTY_MLP_MAXITERATION = "maxIteration";
	public static final Integer DEFAULT_MLP_MAXITERATION = 10000;

	public static final String PROPERTY_MLP_LEARNINGRATE = "learningRate";
	public static final Double DEFAULT_MLP_LEARNINGRATE = 0.1;

	public static final String PROPERTY_MLP_MOMENTUM = "momentum";
	public static final Double DEFAULT_MLP_MOMENTUM = 0.25;

	public static final String PROPERTY_MLP_MAXRESULT = "maxResult";
	public static final Integer DEFAULT_MLP_MAXRESULT = 7;

	public static final String PROPERTY_MLP_VALIDATIONRATE = "validationRate";
	public static final Double DEFAULT_MLP_VALIDATIONRATE = 0.01;

	public static final String PROPERTY_MLP_GENERATEINTEXTCUTOFFSIZE = "generateInTextCutoffSize";
	public static final Integer DEFAULT_MLP_GENERATEINTEXTCUTOFFSIZE = 30;

	public static final String PROPERTY_MLP_LEARNINTEXTCUTOFFSIZE = "learnInTextCutoffSize";
	public static final Integer DEFAULT_MLP_LEARNINTEXTCUTOFFSIZE = 1000;

	public static final String STATUS_GENERATING = "generating";
	public static final String STATUS_LEARNING = "learning";
	public static final String STATUS_COMPLETED = "completed";
	public static final String STATUS_EXCESSIVE = "excessive";

	public static final String DATATYPE_PROCESSPLATFORM = "processPlatform";
	public static final String DATATYPE_CMS = "cms";

	public static final String NEURALNETWORKTYPE_MLP = "mlp";

	public static final String ANALYZETYPE_DEFAULT = "default";
	public static final String ANALYZETYPE_FULL = "full";
	public static final String ANALYZETYPE_CUSTOMIZED = "customized";

	public LinkedHashMap<String, String> getPropertyMap() {
		return this.propertyMap == null ? new LinkedHashMap<String, String>() : this.propertyMap;
	}

	public NeuralNetwork<MomentumBackpropagation> createNeuralNetwork() {
		return createNeuralNetwork(this.getInValueCount(), this.getOutValueCount(), MapTools
				.getInteger(this.getPropertyMap(), PROPERTY_MLP_HIDDENLAYERCOUNT, DEFAULT_MLP_HIDDENLAYERCOUNT));
	}

	public NeuralNetwork<MomentumBackpropagation> createNeuralNetwork(Integer inValueCount, Integer outValueCount,
			Integer hiddenLayerCount) {
		NeuronProperties inputNeuronProperties = new NeuronProperties(InputNeuron.class, Linear.class);
		NeuronProperties hiddenNeuronProperties = new NeuronProperties(InputOutputNeuron.class, WeightedSum.class,
				Sigmoid.class);
		NeuronProperties outputNeuronProperties = new NeuronProperties(InputOutputNeuron.class, WeightedSum.class,
				Sigmoid.class);
		NeuralNetwork<MomentumBackpropagation> neuralNetwork = new NeuralNetwork<>();
		neuralNetwork.setNetworkType(NeuralNetworkType.MULTI_LAYER_PERCEPTRON);
		Layer inputLayer = LayerFactory.createLayer(inValueCount, inputNeuronProperties);
		inputLayer.addNeuron(new BiasNeuron());
		neuralNetwork.addLayer(inputLayer);
		List<Integer> hiddenNeurons = this.hiddenNeurons(inValueCount, outValueCount, hiddenLayerCount);
		for (Integer count : hiddenNeurons) {
			Layer layer = LayerFactory.createLayer(count, hiddenNeuronProperties);
			layer.addNeuron(new BiasNeuron());
			neuralNetwork.addLayer(layer);
		}
		Layer outputLayer = LayerFactory.createLayer(outValueCount, outputNeuronProperties);
		neuralNetwork.addLayer(outputLayer);
		for (int i = 0; i < (neuralNetwork.getLayersCount() - 1); i++) {
			Layer prevLayer = neuralNetwork.getLayers().get(i);
			Layer nextLayer = neuralNetwork.getLayers().get(i + 1);
			ConnectionFactory.fullConnect(prevLayer, nextLayer);
		}
		neuralNetwork.setLearningRule(this.createMomentumBackpropagation(
				MapTools.getDouble(this.getPropertyMap(), PROPERTY_MLP_MAXERROR, DEFAULT_MLP_MAXERROR),
				MapTools.getInteger(this.getPropertyMap(), PROPERTY_MLP_MAXITERATION, DEFAULT_MLP_MAXITERATION),
				MapTools.getDouble(this.getPropertyMap(), PROPERTY_MLP_LEARNINGRATE, DEFAULT_MLP_LEARNINGRATE),
				MapTools.getDouble(this.getPropertyMap(), PROPERTY_MLP_MOMENTUM, DEFAULT_MLP_MOMENTUM)));
		NeuralNetworkFactory.setDefaultIO(neuralNetwork);
		neuralNetwork.randomizeWeights();
		return neuralNetwork;
	}

	private MomentumBackpropagation createMomentumBackpropagation(Double maxError, Integer maxIteration,
			Double learningRate, Double momentum) {
		MomentumBackpropagation momentumBackpropagation = new MomentumBackpropagation();
		momentumBackpropagation.setMaxError(maxError);
		momentumBackpropagation.setMaxIterations(maxIteration);
		momentumBackpropagation.setLearningRate(learningRate);
		momentumBackpropagation.setMomentum(momentum);
		return momentumBackpropagation;
	}

	private List<Integer> hiddenNeurons(Integer inSize, Integer outSize, Integer hiddenLayerCount) {
		List<Integer> list = new ArrayList<Integer>();
		int count = inSize.intValue();
		for (int i = 0; i < hiddenLayerCount; i++) {
			count = (count + outSize) / 2;
			list.add(new Integer(count));
		}
		return list;
	}

	public static final String neuralNetworkType_FIELDNAME = "neuralNetworkType";
	@FieldDescribe("신경망 유형:" + NEURALNETWORKTYPE_MLP + ".")
	@Column(length = length_32B, name = ColumnNamePrefix + neuralNetworkType_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false)
	private String neuralNetworkType;

	public static final String dataType_FIELDNAME = "dataType";
	@FieldDescribe("데이터 유형:" + DATATYPE_CMS + "," + DATATYPE_PROCESSPLATFORM)
	@Column(length = length_32B, name = ColumnNamePrefix + dataType_FIELDNAME)
	@CheckPersist(allowEmpty = true, simplyString = false)
	private String dataType;

	public static final String name_FIELDNAME = "name";
	@Flag
	@FieldDescribe("이름")
	@Column(length = length_255B, name = ColumnNamePrefix + name_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + name_FIELDNAME)
	@CheckPersist(allowEmpty = false, citationNotExists =
	/* 이름 중복 검사 */
	@CitationNotExist(type = Model.class, fields = { "id", "name", "alias" }))
	private String name;

	public static final String description_FIELDNAME = "description";
	@FieldDescribe("설명")
	@Column(length = length_255B, name = ColumnNamePrefix + description_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String description;

	public static final String alias_FIELDNAME = "alias";
	@Flag
	@FieldDescribe("별칭.")
	@Column(length = length_255B, name = ColumnNamePrefix + alias_FIELDNAME)
	@CheckPersist(allowEmpty = true, citationNotExists =
	/* 같은 앱 안에서 이름 중복 불가 */
	@CitationNotExist(type = Model.class, fields = { "id", "name", "alias" }))
	private String alias;

	public static final String inValueScriptText_FIELDNAME = "inValueScriptText";
	@FieldDescribe("입력값 스크립트, 스크립트의 조작 대상은 inValues, 객체 유형 Set")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + inValueScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String inValueScriptText;

	public static final String outValueScriptText_FIELDNAME = "outValueScriptText";
	@FieldDescribe("출력값 스크립트, 스크립트의 조작 대상은 outValues, 객체 유형 Set")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + outValueScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String outValueScriptText;

	public static final String attachmentScriptText_FIELDNAME = "attachmentScriptText";
	@FieldDescribe("첨부파일 필터 스크립트, 스크립트의 조작 대상은 attachments, 객체 유형 List, 내용은 첨부파일 이름이며 형태소 분석이 필요 없는 첨부파일 이름을 삭제할 수 있음.")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_10M, name = ColumnNamePrefix + attachmentScriptText_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String attachmentScriptText;

	public static final String processList_FIELDNAME = "processList";
	@FieldDescribe("포함 프로세스.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + processList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + processList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + processList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + processList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> processList;

	public static final String applicationList_FIELDNAME = "applicationList";
	@FieldDescribe("포함 앱.")
	@PersistentCollection(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + applicationList_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + applicationList_FIELDNAME + JoinIndexNameSuffix))
	@OrderColumn(name = ORDERCOLUMNCOLUMN)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + applicationList_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + applicationList_FIELDNAME + ElementIndexNameSuffix)
	@CheckPersist(allowEmpty = true)
	private List<String> applicationList;

	public static final String analyzeType_FIELDNAME = "analyzeType";
	@FieldDescribe("분석 유형, default, full, customized")
	@Column(length = length_32B, name = ColumnNamePrefix + analyzeType_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String analyzeType;

	public static final String maxResult_FIELDNAME = "maxResult";
	@FieldDescribe("최대 출력값")
	@Column(name = ColumnNamePrefix + maxResult_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer maxResult;

	public static final String inValueCount_FIELDNAME = "inValueCount";
	@FieldDescribe("입력 개수")
	@Column(name = ColumnNamePrefix + inValueCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer inValueCount;

	public static final String outValueCount_FIELDNAME = "outValueCount";
	@FieldDescribe("출력 개수")
	@Column(name = ColumnNamePrefix + outValueCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer outValueCount;

	public static final String propertyMap_FIELDNAME = "propertyMap";
	@FieldDescribe("신경망 매개변수.")
	@CheckPersist(allowEmpty = true)
	@PersistentMap(fetch = FetchType.EAGER)
	@ContainerTable(name = TABLE + ContainerTableNameMiddle + propertyMap_FIELDNAME, joinIndex = @Index(name = TABLE
			+ IndexNameMiddle + propertyMap_FIELDNAME + JoinIndexNameSuffix))
	@KeyColumn(name = ColumnNamePrefix + key_FIELDNAME)
	@ElementColumn(length = length_255B, name = ColumnNamePrefix + propertyMap_FIELDNAME)
	@ElementIndex(name = TABLE + IndexNameMiddle + propertyMap_FIELDNAME + ElementIndexNameSuffix)
	@KeyIndex(name = TABLE + IndexNameMiddle + propertyMap_FIELDNAME + KeyIndexNameSuffix)
	private LinkedHashMap<String, String> propertyMap;

	public static final String startDate_FIELDNAME = "startDate";
	@FieldDescribe("시작 시간")
	@Column(name = ColumnNamePrefix + startDate_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + startDate_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date startDate;

	public static final String endDate_FIELDNAME = "endDate";
	@FieldDescribe("시작 시간")
	@Column(name = ColumnNamePrefix + endDate_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + endDate_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Date endDate;

	public static final String entryCount_FIELDNAME = "entryCount";
	@FieldDescribe("항목 개수")
	@Column(name = ColumnNamePrefix + entryCount_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + entryCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer entryCount;

	public static final String effectiveEntryCount_FIELDNAME = "effectiveEntryCount";
	@FieldDescribe("유효 항목 개수")
	@Column(name = ColumnNamePrefix + effectiveEntryCount_FIELDNAME)
	@Index(name = TABLE + IndexNameMiddle + effectiveEntryCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer effectiveEntryCount;

	public static final String status_FIELDNAME = "status";
	@FieldDescribe("상태, generating or learning")
	@Column(length = length_32B, name = ColumnNamePrefix + status_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String status;

	public static final String nnet_FIELDNAME = "nnet";
	@FieldDescribe("방안 바이너리 코드")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_100M, name = ColumnNamePrefix + nnet_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String nnet;

	public static final String intermediateNnet_FIELDNAME = "intermediateNnet";
	@FieldDescribe("방안 바이너리 코드")
	@Lob
	@Basic(fetch = FetchType.EAGER)
	@Column(length = JpaObject.length_100M, name = ColumnNamePrefix + intermediateNnet_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private String intermediateNnet;

	public static final String generatingPercent_FIELDNAME = "generatingPercent";
	@FieldDescribe("현재 항목 생성 진행률")
	@Column(name = ColumnNamePrefix + generatingPercent_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer generatingPercent;

	public static final String totalError_FIELDNAME = "totalError";
	@FieldDescribe("학습 총 오차, 현재 평균 제곱 오차")
	@Column(name = ColumnNamePrefix + totalError_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double totalError;

	public static final String learnEntryCount_FIELDNAME = "learnEntryCount";
	@FieldDescribe("학습 항목 개수")
	@Column(name = ColumnNamePrefix + learnEntryCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer learnEntryCount;

	public static final String validationEntryCount_FIELDNAME = "validationEntryCount";
	@FieldDescribe("테스트 항목 개수")
	@Column(name = ColumnNamePrefix + validationEntryCount_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Integer validationEntryCount;

	public static final String validationMeanSquareError_FIELDNAME = "validationMeanSquareError";
	@FieldDescribe("테스트 결과")
	@Column(name = ColumnNamePrefix + validationMeanSquareError_FIELDNAME)
	@CheckPersist(allowEmpty = true)
	private Double validationMeanSquareError;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getOutValueScriptText() {
		return outValueScriptText;
	}

	public void setOutValueScriptText(String outValueScriptText) {
		this.outValueScriptText = outValueScriptText;
	}

	public String getInValueScriptText() {
		return inValueScriptText;
	}

	public void setInValueScriptText(String inValueScriptText) {
		this.inValueScriptText = inValueScriptText;
	}

	public List<String> getProcessList() {
		return processList;
	}

	public void setProcessList(List<String> processList) {
		this.processList = processList;
	}

	public List<String> getApplicationList() {
		return applicationList;
	}

	public void setApplicationList(List<String> applicationList) {
		this.applicationList = applicationList;
	}

	public Integer getEntryCount() {
		return entryCount;
	}

	public void setEntryCount(Integer entryCount) {
		this.entryCount = entryCount;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getNnet() {
		return nnet;
	}

	public void setNnet(String nnet) {
		this.nnet = nnet;
	}

	public Integer getInValueCount() {
		return inValueCount;
	}

	public void setInValueCount(Integer inValueCount) {
		this.inValueCount = inValueCount;
	}

	public Integer getOutValueCount() {
		return outValueCount;
	}

	public void setOutValueCount(Integer outValueCount) {
		this.outValueCount = outValueCount;
	}

	public Integer getMaxResult() {
		return maxResult;
	}

	public void setMaxResult(Integer maxResult) {
		this.maxResult = maxResult;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getAttachmentScriptText() {
		return attachmentScriptText;
	}

	public void setAttachmentScriptText(String attachmentScriptText) {
		this.attachmentScriptText = attachmentScriptText;
	}

	public Integer getLearnEntryCount() {
		return learnEntryCount;
	}

	public void setLearnEntryCount(Integer learnEntryCount) {
		this.learnEntryCount = learnEntryCount;
	}

	public String getAnalyzeType() {
		return analyzeType;
	}

	public void setAnalyzeType(String analyzeType) {
		this.analyzeType = analyzeType;
	}

	public String getIntermediateNnet() {
		return intermediateNnet;
	}

	public void setIntermediateNnet(String intermediateNnet) {
		this.intermediateNnet = intermediateNnet;
	}

	public void setTotalError(Double totalError) {
		this.totalError = totalError;
	}

	public Integer getGeneratingPercent() {
		return generatingPercent;
	}

	public Double getTotalError() {
		return totalError;
	}

	public void setGeneratingPercent(Integer generatingPercent) {
		this.generatingPercent = generatingPercent;
	}

	public String getNeuralNetworkType() {
		return neuralNetworkType;
	}

	public void setNeuralNetworkType(String neuralNetworkType) {
		this.neuralNetworkType = neuralNetworkType;
	}

	public Integer getEffectiveEntryCount() {
		return effectiveEntryCount;
	}

	public void setEffectiveEntryCount(Integer effectiveEntryCount) {
		this.effectiveEntryCount = effectiveEntryCount;
	}

	public Integer getValidationEntryCount() {
		return validationEntryCount;
	}

	public void setValidationEntryCount(Integer validationEntryCount) {
		this.validationEntryCount = validationEntryCount;
	}

	public Double getValidationMeanSquareError() {
		return validationMeanSquareError;
	}

	public void setValidationMeanSquareError(Double validationMeanSquareError) {
		this.validationMeanSquareError = validationMeanSquareError;
	}

	public void setPropertyMap(LinkedHashMap<String, String> propertyMap) {
		this.propertyMap = propertyMap;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}
