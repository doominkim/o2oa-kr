package com.x.ai.assemble.control.bean;

import com.x.base.core.project.annotation.FieldDescribe;
import com.x.base.core.project.gson.GsonPropertyObject;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author chengjian
 * @date 2025/06/06 14:03
 **/
public class McpConfig extends GsonPropertyObject {
    public static final String TYPE_HTTP = "http";

    @FieldDescribe("이름.")
    private String name;

    @FieldDescribe("표시 이름.")
    private String displayName;

    @FieldDescribe("그룹.")
    private String category;

    @FieldDescribe("설명.")
    private String desc;

    @FieldDescribe("사용 여부.")
    private Boolean enable;

    @FieldDescribe("id.")
    private String id;

    @FieldDescribe("http 옵션: {\n"
            + "    \"url\": \"https://xxx/x_program_center/jaxrs/invoke/createMeeting/execute\",\n"
            + "    \"method\": \"post\",\n"
            + "    \"headerMap\": {\n"
            + "      \"Content-Type\": \"application/json\"\n"
            + "    },\n"
            + "    \"bodyMap\": {\n"
            + "      \"subject\": \"${subject}\",\n"
            + "    }.")
    private HttpOption httpOption;

    @FieldDescribe("mcp 매개변수 목록: [\n"
            + "    {\n"
            + "      \"name\": \"subject\",\n"
            + "      \"desc\": \"회의 주제, 회의의 주요 의제나 내용 설명.\",\n"
            + "      \"type\": \"string\",\n"
            + "      \"required\": true\n"
            + "    },\n"
            + "  ].")
    private List<McpParameter> mcpParameterList;

    @FieldDescribe("생성 시간.")
    private Date createDateTime;

    @FieldDescribe("수정 시간.")
    private Date updateDateTime;

    @FieldDescribe("확장 매개변수, json 객체.")
    private Map<String, Object> extra;

    private String type = "http";



    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public HttpOption getHttpOption() {
        return httpOption;
    }

    public void setHttpOption(HttpOption httpOption) {
        this.httpOption = httpOption;
    }

    public List<McpParameter> getMcpParameterList() {
        return mcpParameterList;
    }

    public void setMcpParameterList(
            List<McpParameter> mcpParameterList) {
        this.mcpParameterList = mcpParameterList;
    }

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Date getUpdateDateTime() {
        return updateDateTime;
    }

    public void setUpdateDateTime(Date updateDateTime) {
        this.updateDateTime = updateDateTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public static class McpParameter extends GsonPropertyObject{
        @FieldDescribe("매개변수 이름.")
        private String name;

        @FieldDescribe("매개변수 설명.")
        private String desc;

        @FieldDescribe("매개변수 유형.")
        private String type;

        @FieldDescribe("필수 여부.")
        private Boolean required;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Boolean getRequired() {
            return required;
        }

        public void setRequired(Boolean required) {
            this.required = required;
        }
    }

    public static class HttpOption extends GsonPropertyObject{
        @FieldDescribe("URL 주소.")
        private String url;

        @FieldDescribe("http 메서드.")
        private String method;

        @FieldDescribe("http 헤더, ${}로 매개변수 치환.")
        private Map<String,String> headerMap;

        @FieldDescribe("http 본문, ${}로 매개변수 치환.")
        private Map<String,String> bodyMap;

        private Boolean internalEnable = false;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public Map<String, String> getHeaderMap() {
            return headerMap;
        }

        public void setHeaderMap(Map<String, String> headerMap) {
            this.headerMap = headerMap;
        }

        public Map<String, String> getBodyMap() {
            return bodyMap;
        }

        public void setBodyMap(Map<String, String> bodyMap) {
            this.bodyMap = bodyMap;
        }

        public Boolean getInternalEnable() {
            return internalEnable;
        }

        public void setInternalEnable(Boolean internalEnable) {
            this.internalEnable = internalEnable;
        }
    }
}
