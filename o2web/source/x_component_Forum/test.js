this.org = {
    //그룹***************

    //하위 그룹 조회--그룹 객체 배열 반환
    //nested  불리언  true 중첩 하위; false 직접 하위; 기본값 false;
    listSubGroup: function(name, nested){
        getOrgActions();
        var data = {"groupList": getNameFlag(name)};
        var v = null;
        if (nested){
            orgActions.listSubGroupNested(data, function(json){v = json.data;}, null, false);
        }else{
            orgActions.listSubGroupDirect(data, function(json){v = json.data;}, null, false);
        }
        return v;
    },
    //상위 그룹 조회--그룹 객체 배열 반환
    //nested  불리언  true 중첩 상위; false 직접 상위; 기본값 false;
    listSupGroup:function(name, nested){
        getOrgActions();
        var data = {"groupList": getNameFlag(name)};
        var v = null;
        if (nested){
            orgActions.listSupGroupNested(data, function(json){v = json.data;}, null, false);
        }else{
            orgActions.listSupGroupDirect(data, function(json){v = json.data;}, null, false);
        }
        return v;
    },
    //사용자 소속 그룹(중첩)--그룹 객체 배열 반환
    listGroupWithPerson:function(name){
        getOrgActions();
        var data = {"personList": getNameFlag(name)};
        var v = null;
        orgActions.listGroupWithPerson(data, function(json){v = json.data;}, null, false);
        return v;
    },
    //그룹의 역할 보유 여부--true, false 반환
    groupHasRole: function(name, role){
        getOrgActions();
        nameFlag = (typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
        var data = {"group":nameFlag,"roleList":getNameFlag(role)};
        var v = false;
        orgActions.groupHasRole(data, function(json){v = json.data.value;}, null, false);
        return v;
    },

    //역할***************
    //역할 조회--역할 객체 배열 반환
    getRole: function(name){
        getOrgActions();
        var data = {"roleList": getNameFlag(name)};
        var v = null;
        orgActions.listRole(data, function(json){v = json.data;}, null, false);
        return (v && v.length===1) ? v[0] : v;
    },
    //사용자의 모든 역할(중첩)--역할 객체 배열 반환
    listRoleWithPerson:function(name){
        getOrgActions();
        var data = {"personList": getNameFlag(name)};
        var v = null;
        orgActions.listRoleWithPerson(data, function(json){v = json.data;}, null, false);
        return v;
    },

    //사용자***************
    //사용자의 역할 보유 여부--true, false 반환
    personHasRole: function(name, role){
        getOrgActions();
        nameFlag = (typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
        var data = {"person":nameFlag,"roleList":getNameFlag(role)};
        var v = false;
        orgActions.personHasRole(data, function(json){v = json.data.value;}, null, false);
        return v;
    },


    //하위 사용자 조회--사용자 객체 배열 반환
    //nested  불리언  true 중첩 하위; false 직접 하위; 기본값 false;
    listSubPerson: function(name, nested){
        getOrgActions();
        var data = {"personList": getNameFlag(name)};
        var v = null;
        if (nested){
            orgActions.listPersonSubNested(data, function(json){v = json.data;}, null, false);
        }else{
            orgActions.listPersonSubDirect(data, function(json){v = json.data;}, null, false);
        }
        return v;
    },
    //상위 사용자 조회--사용자 객체 배열 반환
    //nested  불리언  true 중첩 상위; false 직접 상위; 기본값 false;
    listSupPerson: function(name, nested){
        getOrgActions();
        var data = {"personList": getNameFlag(name)};
        var v = null;
        if (nested){
            orgActions.listPersonSupNested(data, function(json){v = json.data;}, null, false);
        }else{
            orgActions.listPersonSupDirect(data, function(json){v = json.data;}, null, false);
        }
        return v;
    },
    //그룹의 모든 사용자 조회--사용자 객체 배열 반환
    listPersonWithGroup: function(name){
        getOrgActions();
        var data = {"groupList": getNameFlag(name)};
        var v = null;
        orgActions.listPersonWithGroup(data, function(json){v = json.data;}, null, false);
        return v;
    },
    //역할의 모든 사용자 조회--사용자 객체 배열 반환
    listPersonWithRole: function(name){
        getOrgActions();
        var data = {"roleList": getNameFlag(name)};
        var v = null;
        orgActions.listPersonWithRole(data, function(json){v = json.data;}, null, false);
        return v;
    },
    //신분의 모든 사용자 조회--사용자 객체 배열 반환
    listPersonWithIdentity: function(name){
        getOrgActions();
        var data = {"identityList": getNameFlag(name)};
        var v = null;
        orgActions.listPersonWithIdentity(data, function(json){v = json.data;}, null, false);
        return v;
    },
    //신분의 모든 사용자 조회--사용자 객체 배열 또는 사용자 객체 반환
    getPersonWithIdentity: function(name){
        getOrgActions();
        var data = {"identityList": getNameFlag(name)};
        var v = null;
        orgActions.listPersonWithIdentity(data, function(json){v = json.data;}, null, false);
        return (v && v.length===1) ? v[0] : v;
    },
    //조직 구성원 사용자 조회--사용자 객체 배열 반환
    //nested  불리언  true 중첩된 모든 구성원; false 직접 구성원; 기본값 false;
    listPersonWithUnit: function(name, nested){
        getOrgActions();
        var data = {"unitList": getNameFlag(name)};
        var v = null;
        if (nested){
            orgActions.listPersonWithUnitNested(data, function(json){v = json.data;}, null, false);
        }else{
            orgActions.listPersonWithUnitDirect(data, function(json){v = json.data;}, null, false);
        }
        return v;
    },

    //사용자 속성************
    //사용자 속성 값 추가(속성에 values 값 추가, 해당 속성이 없으면 생성)
    appendPersonAttribute: function(person, attr, values){
        getOrgActions();
        var personFlag = (typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        var data = {"attributeList":values,"name":attr,"person":personFlag};
        orgActions.appendPersonAttribute(data, function(json){
            if (json.data.value){
                if (success) success();
            }else{
                if (failure) failure(null, "", "append values failed");
            }
        }, function(xhr, text, error){
            if (failure) failure(xhr, text, error);
        }, false);
    },
    //사용자 속성 값 설정(속성 값을 values로 변경, 해당 속성이 없으면 생성)
    setPersonAttribute: function(person, attr, values){
        getOrgActions();
        var personFlag = (typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        var data = {"attributeList":values,"name":attr,"person":personFlag};
        orgActions.setPersonAttribute(data, function(json){
            if (json.data.value){
                if (success) success();
            }else{
                if (failure) failure(null, "", "append values failed");
            }
        }, function(xhr, text, error){
            if (failure) failure(xhr, text, error);
        }, false);
    },
    //사용자 속성 값 조회
    getPersonAttribute: function(person, attr){
        getOrgActions();
        var personFlag = (typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        var data = {"name":attr,"person":personFlag};
        var v = null;
        orgActions.getPersonAttribute(data, function(json){v = json.data.attributeList;}, null, false);
        return v;
    },
    //사용자의 모든 속성 이름 목록
    listPersonAttributeName: function(name){
        getOrgActions();
        var data = {"personList":getNameFlag(name)};
        var v = null;
        orgActions.listPersonAttributeName(data, function(json){v = json.data.nameList;}, null, false);
        return v;
    },
    //사용자의 모든 속성 목록
    listPersonAllAttribute: function(name){
        getOrgActions();
        var data = {"personList":getNameFlag(name)};
        var v = null;
        orgActions.listPersonAllAttribute(data, function(json){v = json.data;}, null, false);
        return v;
    },



    //조직**********
    //조직 조회
    getUnit: function(name){
        getOrgActions();
        var data = {"unitList":getNameFlag(name)};
        var v = null;
        orgActions.listUnit(data, function(json){v = json.data;}, null, false);
        return (v && v.length===1) ? v[0] : v;
    },
    //조직의 하위 조회--조직 객체 배열 반환
    //nested  불리언  true 중첩 하위; false 직접 하위; 기본값 false;
    listSubUnit: function(name, nested){
        getOrgActions();
        var data = {"unitList": getNameFlag(name)};
        var v = null;
        if (nested){
            orgActions.listUnitSubNested(data, function(json){v = json.data;}, null, false);
        }else{
            orgActions.listUnitSubDirect(data, function(json){v = json.data;}, null, false);
        }
        return v;
    },
    //조직의 상위 조회--조직 객체 배열 반환
    //nested  불리언  true 중첩 상위; false 직접 상위; 기본값 false;
    listSupUnit: function(name, nested){
        getOrgActions();
        var data = {"unitList": getNameFlag(name)};
        var v = null;
        if (nested){
            orgActions.listUnitSupNested(data, function(json){v = json.data;}, null, false);
        }else{
            orgActions.listUnitSupDirect(data, function(json){v = json.data;}, null, false);
        }
        return v;
    },
    //개인 신분으로 조직 조회
    //flag 숫자    몇 번째 단계의 조직을 조회할지 나타냄
    //     문자열  지정 유형의 조직 조회를 나타냄
    //     비어 있음     직접 소속 조직 조회를 나타냄
    getUnitByIdentity: function(name, flag){
        getOrgActions();
        var getUnitMethod = "current";
        var v;
        if (flag){
            if (typeOf(flag)==="string") getUnitMethod = "type";
            if (typeOf(flag)==="number") getUnitMethod = "level";
        }
        switch (getUnitMethod){
            case "current":
                var data = {"identityList":getNameFlag(name)};
                orgActions.f(data, function(json){ v = json.data; }, null, false);
                break;
            case "type":
                var data = {"identity":(typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name,"type":flag};
                orgActions.getUnitWithIdentityAndType(data, function(json){ v = json.data; }, null, false);
                break;
            case "level":
                var data = {"identity":(typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name,"level":flag};
                orgActions.getUnitWithIdentityAndLevel(data, function(json){ v = json.data; }, null, false);
                break;
        }
        return v;
    },
    //신분 소속 조직의 모든 상위 조직 목록
    listAllSupUnitWithIdentity: function(name){
        getOrgActions();
        var data = {"identityList":getNameFlag(name)};
        var v = null;
        orgActions.listUnitSupNestedWithIdentity(data, function(json){v = json.data;}, null, false);
        return v;
    },
    //사용자가 소속된 모든 조직 조회
    listUnitWithPerson: function(name){
        getOrgActions();
        var data = {"personList":getNameFlag(name)};
        var v = null;
        orgActions.listUnitWithPerson(data, function(json){v = json.data;}, null, false);
        return v;
    },
    //사용자 소속 조직의 모든 상위 조직 목록
    listAllSupUnitWithPerson: function(name){
        getOrgActions();
        var data = {"personList":getNameFlag(name)};
        var v = null;
        orgActions.listUnitSupNestedWithPerson(data, function(json){v = json.data;}, null, false);
        return v;
    },
    //조직 속성으로 일치하는 모든 조직 조회
    listUnitWithAttribute: function(name, attribute){
        getOrgActions();
        var data = {"name":name,"attribute":attribute};
        var v = null;
        orgActions.listUnitWithAttribute(data, function(json){v = json.data;}, null, false);
        return v;
    },
    //조직 직무로 일치하는 모든 조직 조회
    listUnitWithDuty: function(name, id){
        getOrgActions();
        var data = {"name":name,"identity":(typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id};
        var v = null;
        orgActions.listUnitWithDuty(data, function(json){v = json.data;}, null, false);
        return v;
    },

    //조직 직무***********
    //지정 조직 직무의 신분 조회
    getDuty: function(duty, id){
        getOrgActions();
        var data = {"name":duty,"unit":(typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id};
        var v = null;
        orgActions.getDuty(data, function(json){v = json.data;}, null, false);
        return v;
    },
    //신분의 모든 직무 이름 조회
    listDutyNameWithIdentity: function(name){
        getOrgActions();
        var data = {"identityList":getNameFlag(name)};
        var v = null;
        orgActions.listDutyNameWithIdentity(data, function(json){v = json.data.nameList;}, null, false);
        return v;
    },
    //조직의 모든 직무 이름 조회
    listDutyNameWithUnit: function(name){
        getOrgActions();
        var data = {"unitList":getNameFlag(name)};
        var v = null;
        orgActions.listDutyNameWithUnit(data, function(json){v = json.data.nameList;}, null, false);
        return v;
    },
    //조직의 모든 직무 조회
    listUnitAllDuty: function(name){
        getOrgActions();
        var data = {"unitList":getNameFlag(name)};
        var v = null;
        orgActions.listUnitAllDuty(data, function(json){v = json.data;}, null, false);
        return v;
    },
    //최상위 조직 목록
    listTopUnit: function(){
        var action = MWF.Actions.get("x_organization_assemble_control");
        var v = null;
        action.listTopUnit(function(json){
            v = json.data;
        }, null, false);
        return v;
    },

    //조직 속성**************
    //조직 속성 값 추가(속성에 values 값 추가, 해당 속성이 없으면 생성)
    appendUnitAttribute: function(unit, attr, values){
        getOrgActions();
        var unitFlag = (typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        var data = {"attributeList":values,"name":attr,"unit":unitFlag};
        orgActions.appendUnitAttribute(data, function(json){
            if (json.data.value){
                if (success) success();
            }else{
                if (failure) failure(null, "", "append values failed");
            }
        }, function(xhr, text, error){
            if (failure) failure(xhr, text, error);
        }, false);
    },
    //조직 속성 값 설정(속성 값을 values로 변경, 해당 속성이 없으면 생성)
    setUnitAttribute: function(unit, attr, values){
        getOrgActions();
        var unitFlag = (typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        var data = {"attributeList":values,"name":attr,"unit":unitFlag};
        orgActions.setUnitAttribute(data, function(json){
            if (json.data.value){
                if (success) success();
            }else{
                if (failure) failure(null, "", "append values failed");
            }
        }, function(xhr, text, error){
            if (failure) failure(xhr, text, error);
        }, false);
    },
    //조직 속성 값 조회
    getUnitAttribute: function(unit, attr){
        getOrgActions();
        var unitFlag = (typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        var data = {"name":attr,"unit":unitFlag};
        var v = null;
        orgActions.getUnitAttribute(data, function(json){v = json.data.attributeList;}, null, false);
        return v;
    },
    //조직의 모든 속성 이름 목록
    listUnitAttributeName: function(name){
        getOrgActions();
        var data = {"unitList":getNameFlag(name)};
        var v = null;
        orgActions.listUnitAttributeName(data, function(json){v = json.data.nameList;}, null, false);
        return v;
    },
    //조직의 모든 속성 목록
    listUnitAllAttribute: function(name){
        getOrgActions();
        var data = {"unitList":getNameFlag(name)};
        var v = null;
        orgActions.listUnitAllAttribute(data, function(json){v = json.data;}, null, false);
        return v;
    }
};