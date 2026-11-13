if (!MWF.xScript || !MWF.xScript.PageEnvironment) {
    MWF.xScript = MWF.xScript || {};
    MWF.xScript.PageEnvironment = function (ev) {
        var _data = ev.data;
        var _form = ev.form;
        var _forms = ev.forms;

        this.appType = "portal";

        this.library = COMMON;
        //this.library.version = "4.0";

        //data
        var getJSONData = function (jData) {
            return new MWF.xScript.JSONData(jData, function (data, key, _self) {
                var p = {
                    "getKey": function () {
                        return key;
                    }, "getParent": function () {
                        return _self;
                    }
                };
                while (p && !_forms[p.getKey()]) p = p.getParent();
                var k = (p) ? p.getKey() : "";
                if (k) if(_forms[k]){
                    if(_forms[k].resetData){
                        _forms[k].resetData();
                        if (_forms[k].form){
                            if (_forms[k].form.relatedModules?.[_forms[k].json.id]){
                                _forms[k].form.relatedModules?.[_forms[k].json.id].forEach((module)=>{
                                    module?.reload();
                                })
                            }
                            if (_forms[k].form.relatedDisplayModules && _forms[k].form.relatedDisplayModules[(_forms[k].json.id)]){
                                _forms[k].form.relatedDisplayModules[(_forms[k].json.id)].forEach((o)=>{
                                    o.module?._checkDisplay(o.display);
                                })
                            }
                            if (_forms[k].form.relatedValueModules && _forms[k].form.relatedValueModules[(_forms[k].json.id)]){
                                _forms[k].form.relatedValueModules[(_forms[k].json.id)].forEach((o)=>{
                                    o.module?._checkValue(o.value);
                                })
                            }
                        }
                    }
                } 
                //if (p) if (p.getKey()) if (_forms[p.getKey()]) _forms[p.getKey()].resetData();
            }, '', null, _form);
        };
        this.setData = function (data) {
            this.data = getJSONData(data);
            this.data.save = function (callback) {
                var formData = {
                    "data": data,
                    "sectionList": _form.getSectionList()
                };
                form.workAction.saveData(function (json) {
                    if (callback) callback();
                }.bind(this), null, work.id, jData);
            }
        };
        this.setData(_data);

        //workContext
        this.workContext = {
            "getTask": function () {
                return ev.task;
            },
            "getWork": function () {
                return ev.work || ev.workCompleted;
            },
            "getActivity": function () {
                return ev.activity;
            },
            "getTaskList": function () {
                return ev.taskList;
            },
            "getControl": function () {
                return ev.control;
            },
            "getWorkLogList": function () {
                return ev.workLogList;
            },
            "getAttachmentList": function () {
                return ev.attachmentList;
            },
            "getRouteList": function () {
                return (ev.task) ? ev.task.routeNameList : null;
            },
            "getInquiredRouteList": function () {
                return null;
            },
            "setTitle": function (title) {
                //if (!this.workAction){
                //    MWF.require("MWF.xScript.Actions.WorkActions", null, false);
                //    this.workAction = new MWF.xScript.Actions.WorkActions();
                //}
                //this.workAction.setTitle(ev.work.id, {"title": title});
            }
        };
        var _redefineWorkProperties = function (work) {
            if (work) {
                work.creatorPersonDn = work.creatorPerson;
                work.creatorUnitDn = work.creatorUnit;
                work.creatorUnitDnList = work.creatorUnitList;
                work.creatorIdentityDn = work.creatorIdentity;
                var o = {
                    "creatorPerson": {
                        "get": function () {
                            return this.creatorPersonDn.substring(0, this.creatorPersonDn.indexOf("@"));
                        }
                    },
                    "creatorUnit": {
                        "get": function () {
                            return this.creatorUnitDn.substring(0, this.creatorUnitDn.indexOf("@"));
                        }
                    },
                    "creatorDepartment": {
                        "get": function () {
                            return this.creatorUnitDn.substring(0, this.creatorUnitDn.indexOf("@"));
                        }
                    },
                    "creatorIdentity": {
                        "get": function () {
                            return this.creatorIdentityDn.substring(0, this.creatorIdentityDn.indexOf("@"));
                        }
                    },
                    "creatorUnitList": {
                        "get": function () {
                            var v = [];
                            this.creatorUnitDnList.each(function (dn) {
                                v.push(dn.substring(0, dn.indexOf("@")))
                            });
                            return v;
                        }
                    },
                    "creatorCompany": {
                        "get": function () {
                            return this.creatorUnitList[0]
                        }
                    }
                };
                MWF.defineProperties(work, o);
            }
            return work;
        };
        var _redefineTaskProperties = function (task) {
            if (task) {
                task.personDn = task.person;
                task.unitDn = task.unit;
                task.unitDnList = task.unitList;
                task.identityDn = task.identity;
                var o = {
                    "person": {
                        "get": function () {
                            return this.personDn.substring(0, this.personDn.indexOf("@"));
                        }
                    },
                    "unit": {
                        "get": function () {
                            return this.unitDn.substring(0, this.unitDn.indexOf("@"));
                        }
                    },
                    "department": {
                        "get": function () {
                            return this.unitDn.substring(0, this.unitDn.indexOf("@"));
                        }
                    },
                    "identity": {
                        "get": function () {
                            return this.identityDn.substring(0, this.identityDn.indexOf("@"));
                        }
                    },
                    "unitList": {
                        "get": function () {
                            var v = [];
                            this.unitDnList.each(function (dn) {
                                v.push(dn.substring(0, dn.indexOf("@")))
                            });
                            return v;
                        }
                    },
                    "company": {
                        "get": function () {
                            return this.unitList[0];
                        }
                    }
                };
                MWF.defineProperties(task, o);
            }
            return task;
        };
        _redefineWorkProperties(this.workContext.getWork());
        _redefineTaskProperties(_redefineWorkProperties(this.workContext.getTask()));

        //dict
        this.Dict = MWF.xScript.createDict(_form.json.application, "portal");
        //org
        var orgActions = null;
        var getOrgActions = function () {
            if (!orgActions) {
                MWF.require("MWF.xScript.Actions.UnitActions", null, false);
                orgActions = new MWF.xScript.Actions.UnitActions();
            }
        };
        var getNameFlag = function (name) {
            var t = typeOf(name);
            if (t === "array") {
                var v = [];
                name.each(function (id) {
                    v.push((typeOf(id) === "object") ? (id.distinguishedName || id.id || id.unique || id.name) : id);
                });
                return v;
            } else {
                return [(t === "object") ? (name.distinguishedName || name.id || name.unique || name.name) : name];
            }
        };
        this.org = {
            //그룹***************
            //그룹 조회--그룹 객체 배열 반환
            getGroup: function (name, async) {
                getOrgActions();
                var data = {"groupList": getNameFlag(name)};

                var v = null;

                var cb = function (json) {
                    v = json.data;
                    v = (v && v.length === 1) ? v[0] : v
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listGroup(data, cb, null, !!async);
                return (!!async) ? promise : v;

                // var v = null;
                // orgActions.listGroup(data, function(json){v = json.data;}, null, false);
                // return (v && v.length===1) ? v[0] : v;
            },
            //하위 그룹 조회--그룹 객체 배열 반환
            //nested  불리언  true 중첩 하위; false 직접 하위; 기본값 false;
            listSubGroup: function (name, nested, async) {
                getOrgActions();
                var data = {"groupList": getNameFlag(name)};

                var v = null;
                // var cb = ((async && o2.typeOf(async)=="function") ? (async.isAG ? async : async.ag()) : null) || function(json){
                //     v = json.data;
                //     return v;
                // }.ag().catch(function(json){ return json; });
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise;
                if (nested) {
                    promise = orgActions.listSubGroupNested(data, cb, null, !!async);
                } else {
                    promise = orgActions.listSubGroupDirect(data, cb, null, !!async);
                }
                return (!!async) ? promise : v;

                // var v = null;
                // if (nested){
                //     orgActions.listSubGroupNested(data, function(json){v = json.data;}, null, false);
                // }else{
                //     orgActions.listSubGroupDirect(data, function(json){v = json.data;}, null, false);
                // }
                // return v;
            },
            //상위 그룹 조회--그룹 객체 배열 반환
            //nested  불리언  true 중첩 상위; false 직접 상위; 기본값 false;
            listSupGroup: function (name, nested, async) {
                getOrgActions();
                var data = {"groupList": getNameFlag(name)};

                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise
                if (nested) {
                    var promise = orgActions.listSupGroupNested(data, cb, null, !!async);
                } else {
                    var promise = orgActions.listSupGroupDirect(data, cb, null, !!async);
                }
                return (!!async) ? promise : v;
                // var v = null;
                // if (nested){
                //     orgActions.listSupGroupNested(data, function(json){v = json.data;}, null, false);
                // }else{
                //     orgActions.listSupGroupDirect(data, function(json){v = json.data;}, null, false);
                // }
                // return v;
            },
            //사용자 소속 그룹(중첩)--그룹 객체 배열 반환
            listGroupWithPerson: function (name, async) {
                getOrgActions();
                var data = {"personList": getNameFlag(name)};

                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listGroupWithPerson(data, cb, null, !!async);
                return (!!async) ? promise : v;
                // var v = null;
                // orgActions.listGroupWithPerson(data, function(json){v = json.data;}, null, false);
                // return v;
            },
            //신분 소속 그룹(중첩)--그룹 객체 배열 반환
            listGroupWithIdentity:function(identity, async){
                getOrgActions();
                var data = {"identityList": getNameFlag(identity)};

                var v = null;
                var cb = function(json){
                    v = json.data;
                    if (async && o2.typeOf(async)=="function") return async(v);
                    return v;
                };

                var promise = orgActions.listGroupWithIdentity(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //그룹의 역할 보유 여부--true, false 반환
            groupHasRole: function (name, role, async) {
                getOrgActions();
                nameFlag = (typeOf(name) === "object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
                var data = {"group": nameFlag, "roleList": getNameFlag(role)};

                var v = false;
                var cb = function (json) {
                    v = json.data.value;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.groupHasRole(data, cb, null, !!async);
                return (!!async) ? promise : v;

                // var v = false;
                // orgActions.groupHasRole(data, function(json){v = json.data.value;}, null, false);
                // return v;
            },

            //역할***************
            //역할 조회--역할 객체 배열 반환
            getRole: function (name, async) {
                getOrgActions();
                var data = {"roleList": getNameFlag(name)};

                var v = null;
                var cb = function (json) {
                    v = json.data;
                    v = (v && v.length === 1) ? v[0] : v;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listRole(data, cb, null, !!async);
                return (!!async) ? promise : v;

                // var v = null;
                // orgActions.listRole(data, function(json){v = json.data;}, null, false);
                // return (v && v.length===1) ? v[0] : v;
            },
            //사용자의 모든 역할(중첩)--역할 객체 배열 반환
            listRoleWithPerson: function (name, async) {
                getOrgActions();
                var data = {"personList": getNameFlag(name)};

                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listRoleWithPerson(data, cb, null, !!async);
                return (!!async) ? promise : v;
                // var v = null;
                // orgActions.listRoleWithPerson(data, function(json){v = json.data;}, null, false);
                // return v;
            },

            //사용자***************
            //사용자의 역할 보유 여부--true, false 반환
            personHasRole: function (name, role, async) {
                getOrgActions();
                nameFlag = (typeOf(name) === "object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
                var data = {"person": nameFlag, "roleList": getNameFlag(role)};

                var v = false;
                var cb = function (json) {
                    v = json.data.value;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.personHasRole(data, cb, null, !!async);
                return (!!async) ? promise : v;

                // var v = false;
                // orgActions.personHasRole(data, function(json){v = json.data.value;}, null, false);
                // return v;
            },
            //사용자 조회, 신분, 신분 소속 조직, 개인 소속 그룹, 개인 보유 역할 포함.
            getPersonData: function(name, async){
                getOrgActions();
                var v = null;
                var cb = function(json){
                    v = json.data;
                    if (async && o2.typeOf(async)=="function") return async(v);
                    return v;
                };
                var promise = orgActions.getPerson(null, cb, null, !!async, {"flag": name});
                return (!!async) ? promise : v;
            },
            //사용자 조회--사용자 객체 배열 반환
            getPerson: function (name, async, findCN) {
                getOrgActions();
                var data = {"personList": getNameFlag(name)};
                if( o2.typeOf(findCN) === "boolean"){
                    data.useNameFind = findCN;
                }
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    v = (v && v.length === 1) ? v[0] : v;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listPerson(data, cb, null, !!async);
                return (!!async) ? promise : v;
                // var v = null;
                // orgActions.listPerson(data, function(json){v = json.data;}, null, false);
                // return (v && v.length===1) ? v[0] : v;
            },
            //하위 사용자 조회--사용자 객체 배열 반환
            //nested  불리언  true 중첩 하위; false 직접 하위; 기본값 false;
            listSubPerson: function (name, nested, async) {
                getOrgActions();
                var data = {"personList": getNameFlag(name)};

                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise;
                if (nested) {
                    promise = orgActions.listPersonSubNested(data, cb, null, !!async);
                } else {
                    promise = orgActions.listPersonSubDirect(data, cb, null, !!async);
                }
                return (!!async) ? promise : v;
            },
            //상위 사용자 조회--사용자 객체 배열 반환
            //nested  불리언  true 중첩 상위; false 직접 상위; 기본값 false;
            listSupPerson: function (name, nested, async) {
                getOrgActions();
                var data = {"personList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise;
                if (nested) {
                    promise = orgActions.listPersonSupNested(data, cb, null, !!async);
                } else {
                    promise = orgActions.listPersonSupDirect(data, cb, null, !!async);
                }
                return (!!async) ? promise : v;
            },
            //그룹의 모든 사용자 조회--사용자 객체 배열 반환
            listPersonWithGroup: function (name, async) {
                getOrgActions();
                var data = {"groupList": getNameFlag(name)};

                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listPersonWithGroup(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //역할의 모든 사용자 조회--사용자 객체 배열 반환
            listPersonWithRole: function (name, async) {
                getOrgActions();
                var data = {"roleList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise
                promise = orgActions.listPersonWithRole(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //신분의 모든 사용자 조회--사용자 객체 배열 반환
            listPersonWithIdentity: function (name, async) {
                getOrgActions();
                var data = {"identityList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listPersonWithIdentity(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //신분의 모든 사용자 조회--사용자 객체 배열 또는 사용자 객체 반환
            getPersonWithIdentity: function (name, async) {
                getOrgActions();
                var data = {"identityList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    v = (v && v.length === 1) ? v[0] : v;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listPersonWithIdentity(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //조직 구성원 사용자 조회--사용자 객체 배열 반환
            //nested  불리언  true 중첩된 모든 구성원; false 직접 구성원; 기본값 false;
            listPersonWithUnit: function (name, nested, async) {
                getOrgActions();
                var data = {"unitList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise;
                if (nested) {
                    promise = orgActions.listPersonWithUnitNested(data, cb, null, !!async);
                } else {
                    promise = orgActions.listPersonWithUnitDirect(data, cb, null, !!async);
                }
                return (!!async) ? promise : v;
            },
            //속성으로 사용자 조회--사용자 객체 배열 반환
            //name  string 속성 이름
            //value  string 속성 값
            listPersonWithAttribute: function (name, value, async) {
                getOrgActions();
                var data = {"name": name, "attribute": value};
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listPersonWithAttribute(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //속성으로 사용자 조회--사용자 전체 이름 배열 반환
            //name  string 속성 이름
            //value  string 속성 값
            listPersonNameWithAttribute: function (name, value, async) {
                getOrgActions();
                var data = {"name": name, "attribute": value};
                var v = null;
                var cb = function (json) {
                    v = json.data.personList;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listPersonWithAttributeValue(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },

            //사용자 속성************
            //사용자 속성 값 추가(속성에 values 값 추가, 해당 속성이 없으면 생성)
            appendPersonAttribute: function (person, attr, values, success, failure, async) {
                getOrgActions();
                var personFlag = (typeOf(person) === "object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
                var data = {"attributeList": values, "name": attr, "person": personFlag};

                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };
                var promise = orgActions.appendPersonAttribute(data, cb, null, !!async);
                return (!!async) ? promise : v;

                // var cb = function(json){
                //     if (success) return success(json);
                // }.ag().catch(function(xhr, text, error){
                //     if (failure) return failure(xhr, text, error);
                // });
                //
                // orgActions.appendPersonAttribute(data, cb, null, !!async);
            },
            //사용자 속성 값 설정(속성 값을 values로 변경, 해당 속성이 없으면 생성)
            setPersonAttribute: function (person, attr, values, success, failure, async) {
                getOrgActions();
                var personFlag = (typeOf(person) === "object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
                var data = {"attributeList": values, "name": attr, "person": personFlag};

                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };
                var promise = orgActions.setPersonAttribute(data, cb, null, !!async);
                return (!!async) ? promise : v;

                // var cb = function(json){
                //     if (success) return success(json);
                // }.ag().catch(function(xhr, text, error){
                //     if (failure) return failure(xhr, text, error);
                // });
                //
                // orgActions.setPersonAttribute(data, cb, null, !!async);
            },
            //사용자 속성 값 조회
            getPersonAttribute: function (person, attr, async) {
                getOrgActions();
                var personFlag = (typeOf(person) === "object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
                var data = {"name": attr, "person": personFlag};
                var v = null;
                var cb = function (json) {
                    v = json.data.attributeList;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.getPersonAttribute(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //사용자의 모든 속성 이름 목록
            listPersonAttributeName: function (name, async) {
                getOrgActions();
                var data = {"personList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data.nameList;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listPersonAttributeName(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //사용자의 모든 속성 목록
            listPersonAllAttribute: function (name, async) {
                getOrgActions();
                var data = {"personList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listPersonAllAttribute(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },

            //신분**********
            //신분 조회
            getIdentity: function (name, async) {
                getOrgActions();
                var data = {"identityList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    v = (v && v.length === 1) ? v[0] : v;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listIdentity(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //사용자의 신분 목록
            listIdentityWithPerson: function (name, async, findCN) {
                getOrgActions();
                var data = {"personList": getNameFlag(name)};
                if( o2.typeOf(findCN) === "boolean"){
                    data.useNameFind = findCN;
                }
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listIdentityWithPerson(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //조직 구성원 신분 조회--신분 객체 배열 반환
            //nested  불리언  true 중첩된 모든 구성원; false 직접 구성원; 기본값 false;
            listIdentityWithUnit: function (name, nested, async) {
                getOrgActions();
                var data = {"unitList": getNameFlag(name)};
                var v = null;

                // var cb = function(json){
                //     v = json.data;
                //     if (async && o2.typeOf(async)=="function") return async(v);
                //     return v;
                // }.ag().catch(function(json){ return json; });

                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var method = (nested) ? "listIdentityWithUnitNested" : "listIdentityWithUnitDirect";
                var promise = orgActions[method](data, cb, null, !!async);
                promise.name = "org";

                //
                // if (nested){
                //     orgActions.listIdentityWithUnitNested(data, cb, null, !!async);
                // }else{
                //     orgActions.listIdentityWithUnitDirect(data, cb, null, !!async);
                // }
                return (!!async) ? promise : v;
            },

            //조직**********
            //조직 조회
            getUnit: function (name, async, findCN) {
                getOrgActions();
                var data = {"unitList": getNameFlag(name)};
                if( o2.typeOf(findCN) === "boolean"){
                    data.useNameFind = findCN;
                }
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    v = (v && v.length === 1) ? v[0] : v;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listUnit(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //조직의 하위 조회--조직 객체 배열 반환
            //nested  불리언  true 중첩 하위; false 직접 하위; 기본값 false;
            listSubUnit: function (name, nested, async) {
                getOrgActions();
                var data = {"unitList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise;
                if (nested) {
                    promise = orgActions.listUnitSubNested(data, cb, null, !!async);
                } else {
                    promise = orgActions.listUnitSubDirect(data, cb, null, !!async);
                }
                return (!!async) ? promise : v;
            },
            //조직의 상위 조회--조직 객체 배열 반환
            //nested  불리언  true 중첩 상위; false 직접 상위; 기본값 false;
            //async 불리언 true 비동기 요청
            listSupUnit: function (name, nested, async) {
                getOrgActions();
                var data = {"unitList": getNameFlag(name)};

                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise;
                if (nested) {
                    promise = orgActions.listUnitSupNested(data, cb, null, !!async);
                } else {
                    promise = orgActions.listUnitSupDirect(data, cb, null, !!async);
                }
                return (!!async) ? promise : v;

                // if (callback){
                //     if (nested){
                //         orgActions.listUnitSupNested(data, function(json){v = json.data; o2.runCallback(callback, "success", [v], this);});
                //     }else{
                //         orgActions.listUnitSupDirect(data, function(json){v = json.data; o2.runCallback(callback, "success", [v], this);});
                //     }
                // }else{
                //     var v = null;
                //     if (nested){
                //         orgActions.listUnitSupNested(data, function(json){v = json.data;}, null, false);
                //     }else{
                //         orgActions.listUnitSupDirect(data, function(json){v = json.data;}, null, false);
                //     }
                //     return v;
                // }
            },

            listSupUnitWithLevel: function(name, level, async){
                var v;
                var supUnitList = this.listSupUnit( name, true, !!async);
                var unitList = this.getUnit( name, false, !!async );

                var cb = function(sups, units){
                    v = [].concat(
                        sups,
                        typeOf( units ) === "object" ? [units] : units
                    ).filter(function (u){
                        return u.level === level;
                    });
                    if (async && o2.typeOf(async)==="function") return async(v);
                    return v;
                };

                if( typeof supUnitList.then === 'function' ){
                    return Promise.all([supUnitList, unitList]).then(function( result){
                        return cb(result[0], result[1]);
                    });
                }else{
                    return cb(supUnitList, unitList);
                }
            },

            listSupUnitWithType: function(name, type, async){
                var v;
                var supUnitList = this.listSupUnit( name, true, !!async);
                var unitList = this.getUnit( name, false, !!async );

                var cb = function(sups, units){
                    v = [].concat(
                        sups,
                        typeOf( units ) === "object" ? [units] : units
                    ).filter(function (u){
                        return (u.typeList || []).contains( type );
                    });
                    if (async && o2.typeOf(async)==="function") return async(v);
                    return v;
                };

                if( typeof supUnitList.then === 'function' ){
                    return Promise.all([supUnitList, unitList]).then(function( result){
                        return cb(result[0], result[1]);
                    });
                }else{
                    return cb(supUnitList, unitList);
                }
            },

            //개인 신분으로 조직 조회
            //flag 숫자    몇 번째 단계의 조직을 조회할지 나타냄
            //     문자열  지정 유형의 조직 조회를 나타냄
            //     비어 있음     직접 소속 조직 조회를 나타냄
            getUnitByIdentity: function (name, flag, async) {
                getOrgActions();
                var getUnitMethod = "current";
                var v;
                if (flag) {
                    if (typeOf(flag) === "string") getUnitMethod = "type";
                    if (typeOf(flag) === "number") getUnitMethod = "level";
                }

                var cb;
                var promise;
                switch (getUnitMethod) {
                    case "current":
                        var data = {"identityList": getNameFlag(name)};

                        // var cb = ((async && o2.typeOf(async)=="function") ? (async.isAG ? async : async.ag()) : null) || function(json){
                        //     v = json.data;  v=(v&&v.length===1) ? v[0] : v; return v;
                        // }.ag().catch(function(json){ return json; });


                        cb = function (json) {
                            v = json.data;
                            v = (v && v.length === 1) ? v[0] : v;
                            if (async && o2.typeOf(async) == "function") return async(v);
                            return v;
                        };


                        promise = orgActions.listUnitWithIdentity(data, cb, null, !!async);
                        break;
                    case "type":
                        var data = {
                            "identity": (typeOf(name) === "object") ? (name.distinguishedName || name.id || name.unique || name.name) : name,
                            "type": flag
                        };

                        cb = function (json) {
                            v = json.data;
                            if (async && o2.typeOf(async) == "function") return async(v);
                            return v;
                        };

                        // var cb = ((async && o2.typeOf(async)=="function") ? (async.isAG ? async : async.ag()) : null) || function(json){
                        //     v = json.data;  return v;
                        // }.ag().catch(function(json){ return json; });

                        promise = orgActions.getUnitWithIdentityAndType(data, cb, null, !!async);
                        break;
                    case "level":
                        var data = {
                            "identity": (typeOf(name) === "object") ? (name.distinguishedName || name.id || name.unique || name.name) : name,
                            "level": flag
                        };

                        cb = function (json) {
                            v = json.data;
                            v = (v && v.length === 1) ? v[0] : v;
                            if (async && o2.typeOf(async) == "function") return async(v);
                            return v;
                        };

                        // var cb = ((async && o2.typeOf(async)=="function") ? (async.isAG ? async : async.ag()) : null) || function(json){
                        //     v = json.data;  return v;
                        // }.ag().catch(function(json){ return json; });

                        promise = orgActions.getUnitWithIdentityAndLevel(data, cb, null, !!async);
                        break;
                }
                return (!!async) ? promise : v;
            },
            //신분 소속 조직의 모든 상위 조직 목록
            listAllSupUnitWithIdentity: function (name, async) {
                getOrgActions();
                var data = {"identityList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listUnitSupNestedWithIdentity(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //사용자가 소속된 모든 조직 조회
            listUnitWithPerson: function (name, async) {
                getOrgActions();
                var data = {"personList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listUnitWithPerson(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //사용자 소속 조직의 모든 상위 조직 목록
            listAllSupUnitWithPerson: function (name, async) {
                getOrgActions();
                var data = {"personList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listUnitSupNestedWithPerson(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //조직 속성으로 일치하는 모든 조직 조회
            listUnitWithAttribute: function (name, attribute, async) {
                getOrgActions();
                var data = {"name": name, "attribute": attribute};
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                promise = orgActions.listUnitWithAttribute(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //조직 직무로 일치하는 모든 조직 조회
            listUnitWithDuty: function (name, id, async) {
                getOrgActions();
                var data = {
                    "name": name,
                    "identity": (typeOf(id) === "object") ? (id.distinguishedName || id.id || id.unique || id.name) : id
                };
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listUnitWithDuty(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },

            //조직 직무***********
            //지정 조직 직무의 신분 조회
            getDuty: function (duty, id, async) {
                getOrgActions();
                var data = {
                    "name": duty,
                    "unit": (typeOf(id) === "object") ? (id.distinguishedName || id.id || id.unique || id.name) : id
                };
                var v = null;

                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.getDuty(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //신분의 모든 직무 이름 조회
            listDutyNameWithIdentity: function (name, async) {
                getOrgActions();
                var data = {"identityList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data.nameList;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listDutyNameWithIdentity(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //조직의 모든 직무 이름 조회
            listDutyNameWithUnit: function (name, async) {
                getOrgActions();
                var data = {"unitList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data.nameList;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listDutyNameWithUnit(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //조직의 모든 직무 조회
            listUnitAllDuty: function (name, async) {
                getOrgActions();
                var data = {"unitList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listUnitAllDuty(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //최상위 조직 목록
            listTopUnit: function (async) {
                var action = MWF.Actions.get("x_organization_assemble_control");
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = action.listTopUnit(cb, null, !!async);
                return (!!async) ? promise : v;
            },

            //조직 속성**************
            //조직 속성 값 추가(속성에 values 값 추가, 해당 속성이 없으면 생성)
            appendUnitAttribute: function (unit, attr, values, success, failure, async) {
                getOrgActions();
                var unitFlag = (typeOf(unit) === "object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
                var data = {"attributeList": values, "name": attr, "unit": unitFlag};

                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };
                var promise = orgActions.appendUnitAttribute(data, cb, null, !!async);
                return (!!async) ? promise : v;

                // var cb = function(json){
                //     if (success) return success(json);
                // }.ag().catch(function(xhr, text, error){
                //     if (failure) return failure(xhr, text, error);
                // });
                //
                // orgActions.appendPersonAttribute(data, cb, null, !!async);

                // orgActions.appendUnitAttribute(data, function(json){
                //     if (json.data.value){
                //         if (success) success();
                //     }else{
                //         if (failure) failure(null, "", "append values failed");
                //     }
                // }, function(xhr, text, error){
                //     if (failure) failure(xhr, text, error);
                // }, false);
            },
            //조직 속성 값 설정(속성 값을 values로 변경, 해당 속성이 없으면 생성)
            setUnitAttribute: function (unit, attr, values, success, failure, async) {
                getOrgActions();
                var unitFlag = (typeOf(unit) === "object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
                var data = {"attributeList": values, "name": attr, "unit": unitFlag};

                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };
                var promise = orgActions.setUnitAttribute(data, cb, null, !!async);
                return (!!async) ? promise : v;

                // var cb = function(json){
                //     if (success) return success(json);
                // }.ag().catch(function(xhr, text, error){
                //     if (failure) return failure(xhr, text, error);
                // });
                // orgActions.setUnitAttribute(data, cb, null, !!async);

                // orgActions.setUnitAttribute(data, function(json){
                //     if (json.data.value){
                //         if (success) success();
                //     }else{
                //         if (failure) failure(null, "", "append values failed");
                //     }
                // }, function(xhr, text, error){
                //     if (failure) failure(xhr, text, error);
                // }, false);
            },
            //조직 속성 값 조회
            getUnitAttribute: function (unit, attr, async) {
                getOrgActions();
                var unitFlag = (typeOf(unit) === "object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
                var data = {"name": attr, "unit": unitFlag};
                var v = null;
                var cb = function (json) {
                    v = json.data.attributeList;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.getUnitAttribute(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //조직의 모든 속성 이름 목록
            listUnitAttributeName: function (name, async) {
                getOrgActions();
                var data = {"unitList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data.nameList;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listUnitAttributeName(data, cb, null, !!async);
                return (!!async) ? promise : v;
            },
            //조직의 모든 속성 목록
            listUnitAllAttribute: function (name, async) {
                getOrgActions();
                var data = {"unitList": getNameFlag(name)};
                var v = null;
                var cb = function (json) {
                    v = json.data;
                    if (async && o2.typeOf(async) == "function") return async(v);
                    return v;
                };

                var promise = orgActions.listUnitAllAttribute(data, cb, null, !!async);
                return (!!async) ? promise : v;
            }
        };

        this.Action = (function () {
            var actions = [];
            return function (root, json) {
                var action = actions[root] || (actions[root] = new MWF.xDesktop.Actions.RestActions("", root, ""));
                action.getActions = function (callback) {
                    if (!this.actions) this.actions = {};
                    Object.merge(this.actions, json);
                    if (callback) callback();
                };
                this.invoke = function (option) {
                    action.invoke(option)
                }
            }
        })();
        // this.service = {
        //     "jaxwsClient": {},
        //     "jaxrsClient": {}
        // };

        var lookupAction = null;
        var getLookupAction = function (callback) {
            if (!lookupAction) {
                MWF.require("MWF.xDesktop.Actions.RestActions", function () {
                    lookupAction = new MWF.xDesktop.Actions.RestActions("", "x_processplatform_assemble_surface", "");
                    lookupAction.getActions = function (actionCallback) {
                        this.actions = {
                            //"lookup": {"uri": "/jaxrs/view/flag/{view}/application/flag/{application}"},
                            //"getView": {"uri": "/jaxrs/view/{id}/design"}
                            "lookup": {
                                "uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}/execute",
                                "method": "PUT"
                            },
                            "getView": {"uri": "/jaxrs/queryview/flag/{view}/application/flag/{application}"}
                        };
                        if (actionCallback) actionCallback();
                    }
                    if (callback) callback();
                });
            } else {
                if (callback) callback();
            }
        };

        var _renderViewContainerMobile = function(title, viewerGenerator, okCallback, notCloseOnOK){
            const node = new Element('div.mwf_selectView_node');
            const html = `<div class="mwf_selectView_content invisible" data-o2-element="contentNode">
                            <div class="mwf_selectView_title">${title || ''}</div>
                            <div class="mwf_selectView_view" data-o2-element="viewNode"></div>
                            <div class="mwf_selectView_action">
                                <oo-button type="light" class="mwf_selectView_action_close hide" data-o2-events="click:selectCancel">${o2.LP.widget.close}</oo-button>
                                <oo-button type="light" class="mwf_selectView_action_cancel" data-o2-events="click:selectCancel">${o2.LP.widget.cancel}</oo-button>
                                <oo-button class="mwf_selectView_action_ok" data-o2-events="click:selectOk">${o2.LP.widget.ok}</oo-button>
                            </div>
                          </div>`;
            const o = {
                selectOk: function(e){
                    okCallback && okCallback();
                    if( !notCloseOnOK ){
                        this.selectCancel(e);
                    }
                },
                selectCancel: function(e){
                    this.contentNode.removeClass('visible');
                    this.contentNode.addClass('invisible');
                    window.setTimeout(()=>{
                        node.destroy();
                    }, 200);
                }
            };
            node.loadHtmlText(html, {module: o});
            document.body.appendChild(node);

            // MWF.xDesktop.requireApp("query.Query", "Viewer", ()=>{
            //     viewer = new MWF.xApplication.query.Query.Viewer(o.viewNode, viewJson, {"style": "select"}, _form.app, _form.Macro);
            //     viewer.addEvent('selectRow', (row)=>{
            //         row.node.addClass('selectedRow');
            //     });
            //     viewer.addEvent('unselectRow', (row)=>{
            //         row.node.removeClass('selectedRow');
            //     });
            // });

            window.setTimeout(()=>{
                viewerGenerator(o.viewNode, o);
            }, 200);

            requestAnimationFrame(()=>{
                o.contentNode.removeClass('invisible');
                o.contentNode.addClass('visible');
            });
            o.contentNode.addEventListener('click', (e)=>{
                e.stopPropagation(e);
            });
            node.addEventListener('click', (e)=>{
                o.selectCancel(e);
            });
        }
        this._renderViewContainerMobile = _renderViewContainerMobile;

        var selectViewMobile = function (viewJson, okCallback, dialogOptions, viewOptions, loadedCallback){
            if(!viewOptions)viewOptions = {"style": "select"};
            if(!dialogOptions)dialogOptions = {};

            var viewer = null;
            _renderViewContainerMobile(
                dialogOptions.title || viewJson.caption,
                (viewNode)=>{
                    MWF.xDesktop.requireApp("query.Query", "Viewer", ()=>{
                        viewer = new MWF.xApplication.query.Query.Viewer(viewNode, viewJson, viewOptions, _form.app, _form.Macro);
                        viewer.addEvent('selectRow', (row)=>{
                            row.node.addClass('selectedRow');
                        });
                        viewer.addEvent('unselectRow', (row)=>{
                            row.node.removeClass('selectedRow');
                        });
                        if(loadedCallback)loadedCallback(viewer);
                    });
                },
                ()=>{
                    if(okCallback)okCallback(viewer.getData());
                }
            );
        };
        var selectViewPc = function(viewJson, okCallback, dlalogOptions, viewOptions, loadedCallback){

            if(!viewOptions)viewOptions = {"style": "select"};

            var options =  dlalogOptions || {};
            var width = options.width || viewJson.width || "972";
            var height = options.height || viewJson.height || "672";
            var style = options.style || "v10_view";
            if (layout.mobile){
                var size = document.body.getSize();
                width = size.x;
                height = size.y;
                style = "viewmobile";
            }

            var opts = Object.assign({}, options, {
                title: options.title || viewJson.caption || "select view",
                style: style,
                width: width.toInt(),
                height: height.toInt(),
                zindex: options.zindex,
                html: "<div style='height: 100%;flex:1;'></div>",
                maxHeightPercent: layout.mobile ? "100%" : "98%",
                maskNode: layout.mobile ? $(document.body) :  _form.app.content,
                container: layout.mobile ? $(document.body) : _form.app.content,
                buttonList: [
                    {
                        "text": MWF.LP.process.button.ok,
                        "action": function(){
                            //if (callback) callback(_self.view.selectedItems);
                            if (okCallback) okCallback(_self.view.getData());
                            this.close();
                        }
                    },
                    {
                        "text": MWF.LP.process.button.cancel,
                        "action": function(){this.close();}
                    }
                ]
            });

            var _self = this;
            MWF.require("MWF.xDesktop.Dialog", function(){
                var dlg = o2.DL.open(opts);
                if (layout.mobile){
                    var backAction = dlg.node.getElement(".MWF_dialod_Action_back");
                    var okAction = dlg.node.getElement(".MWF_dialod_Action_ok");
                    if (backAction) backAction.addEvent("click", function(e){
                        dlg.close();
                    }.bind(this));
                    if (okAction) okAction.addEvent("click", function(e){
                        //if (okCallback) okCallback(this.view.selectedItems);
                        if (okCallback) okCallback(this.view.getData());
                        dlg.close();
                    }.bind(this));
                }

                MWF.xDesktop.requireApp("query.Query", "Viewer", function(){
                    this.view = new MWF.xApplication.query.Query.Viewer(dlg.content.getFirst(), viewJson, viewOptions, _form.app, _form.Macro);
                    if(loadedCallback)loadedCallback(this.view);
                }.bind(this));
            }.bind(this));
        };
        this.view = {
            "lookup": function (view, callback, async) {
                var filterList = {"filterList": (view.filter || null)};
                return MWF.Actions.load("x_query_assemble_surface").ViewAction.executeWithQuery(view.view, view.application, filterList, function (json) {
                    var data = {
                        "grid": json.data.grid || json.data.groupGrid,
                        "groupGrid": json.data.groupGrid
                    };
                    if (callback) callback(data);
                    return data;
                }, null, async);
            },

            "lookupV1": function (view, callback) {
                getLookupAction(function () {
                    lookupAction.invoke({
                        "name": "lookup",
                        "async": true,
                        "parameter": {"view": view.view, "application": view.application},
                        "success": function (json) {
                            var data = {
                                "grid": json.data.grid,
                                "groupGrid": json.data.groupGrid
                            };
                            if (callback) callback(data);
                        }.bind(this)
                    });
                }.bind(this));
            },
            "select": function(view, okCallback, dialogOptions, viewOptions, loadedCallback){
                if( view.view || view.viewName || view.name || view.viewId ){
                    var viewJson = {
                        "application": view.application || _form.json.application,
                        "viewName": view.viewName || view.view || view.name || "",
                        "isTitle": typeOf( view.isTitle ) === 'string' ? view.isTitle : ((view.isTitle===false) ? "no" : "yes"),
                        "select": typeOf( view.select ) === 'string' ? view.select : ((view.isMulti===false) ? "single" : "multi"),
                        "filter": view.filter
                    };
                    if( view.hasOwnProperty('viewId') )viewJson.viewId = view.viewId;
                    // if( view.hasOwnProperty('titleStyles') )viewJson.titleStyles = view.titleStyles;
                    // if( view.hasOwnProperty('itemStyles') )viewJson.itemStyles = view.itemStyles;
                    // if( view.hasOwnProperty('isExpand') )viewJson.isExpand = view.isExpand;
                    // if( view.hasOwnProperty('showActionbar') )viewJson.showActionbar = view.showActionbar;
                    // if( view.hasOwnProperty('defaultSelectedScript') )viewJson.defaultSelectedScript = view.defaultSelectedScript;
                    // if( view.hasOwnProperty('selectedAbleScript') )viewJson.selectedAbleScript = view.selectedAbleScript;
                    for( var key in view){
                        if( !viewJson.hasOwnProperty(key) ){
                            viewJson[key] = view[key];
                        }
                    }
                    //if (layout.mobile && o2.version.dev===10){
                    var styleType = _form.json?.formStyleType || _form.viewJson?.viewStyleType;
                    if (layout.mobile && styleType === 'v10') {
                        selectViewMobile(viewJson, okCallback, dialogOptions, viewOptions, loadedCallback);
                    }else{
                        selectViewPc(viewJson, okCallback, dialogOptions, viewOptions, loadedCallback);
                    }
                }
            }
        }


        var selectStatementMobile = function (statementJson, okCallback, dialogOptions, statementOptions, loadedCallback){
            if(!statementOptions)statementOptions = {"style": "select"};
            if(!dialogOptions)dialogOptions = {};

            var viewer = null;
            _renderViewContainerMobile(
                dialogOptions.title || statementJson.caption,
                (viewNode)=>{
                    MWF.xDesktop.requireApp("query.Query", "Statement", ()=>{
                        viewer = new MWF.xApplication.query.Query.Statement( viewNode, statementJson, statementOptions, _form.app, _form.Macro);
                        viewer.addEvent('selectRow', (row)=>{
                            row.node.addClass('selectedRow');
                        });
                        viewer.addEvent('unselectRow', (row)=>{
                            row.node.removeClass('selectedRow');
                        });
                        if(loadedCallback)loadedCallback(viewer);
                    });
                },
                ()=>{
                    if(okCallback)okCallback(viewer.getData());
                }
            );
        };
        var selectStatementPc = function(statementJson, okCallback, dialogOptions, statementOptions, loadedCallback){

            if(!statementOptions)statementOptions = {"style": "select"};

            var options =  dialogOptions || {};
            var width = options.width || statementJson.width || "972";
            var height = options.height || statementJson.height || "672";
            var style = options.style || "v10_view";

            if (layout.mobile) {
                var size = document.body.getSize();
                width = size.x;
                height = size.y;
                style = "viewmobile";
            }

            var opts = Object.assign({}, options, {
                title: options.title || statementJson.caption || "select view",
                style: style,
                width: width.toInt(),
                height: height.toInt(),
                zindex: options.zindex,
                html: "<div style='height: 100%;flex:1;'></div>",
                maxHeightPercent: layout.mobile ? "100%" : "98%",
                maskNode: layout.mobile ? $(document.body) :  _form.app.content,
                container: layout.mobile ? $(document.body) : _form.app.content,
                buttonList: [
                    {
                        "text": MWF.LP.process.button.ok,
                        "action": function () {
                            //if (callback) callback(_self.view.selectedItems);
                            if (okCallback) okCallback(_self.statement.getData());
                            this.close();
                        }
                    },
                    {
                        "text": MWF.LP.process.button.cancel,
                        "action": function () {
                            this.close();
                        }
                    }
                ]
            });
            var _self = this;
            MWF.require("MWF.xDesktop.Dialog", function () {
                var dlg = o2.DL.open(opts);
                if (layout.mobile) {
                    var backAction = dlg.node.getElement(".MWF_dialod_Action_back");
                    var okAction = dlg.node.getElement(".MWF_dialod_Action_ok");
                    if (backAction) backAction.addEvent("click", function (e) {
                        dlg.close();
                    }.bind(this));
                    if (okAction) okAction.addEvent("click", function (e) {
                        if (okCallback) okCallback(this.statement.getData());
                        dlg.close();
                    }.bind(this));
                }

                MWF.xDesktop.requireApp("query.Query", "Statement", function () {
                    this.statement = new MWF.xApplication.query.Query.Statement(dlg.content.getFirst(), statementJson, statementOptions, _form.app, _form.Macro);
                    if(loadedCallback)loadedCallback(this.statement);
                }.bind(this));
            }.bind(this));
        };

        this.statement = {
            execute: function (obj, callback, async) {
                if( obj.format ){
                    return this._execute(obj, callback, async, obj.format);
                }else{
                    if( this.needCheckFormat(obj) ){
                        var result;
                        var p = MWF.Actions.load("x_query_assemble_surface").StatementAction.getFormat(obj.name, function(json){
                            result = this._execute(obj, callback, async, json.data.format);
                            return result;
                        }.bind(this), null, async);
                        return result || p;
                    }else{
                        return this._execute(obj, callback, async, "");
                    }

                }
            },
            needCheckFormat: function(s){
                if( s.format )return false;
                if( typeOf(s.parameter) === "object" ){
                    for( var p in s.parameter ){
                        if( typeOf( s.parameter[p] ) === "date" )return true;
                    }
                }
                if( typeOf(s.filter) === "array" ){
                    for( var i=0; i< s.filter.length; i++){
                        var fType = s.filter[i].formatType;
                        if( ["dateTimeValue", "datetimeValue", "dateValue", "timeValue"].contains( fType ) )return true;
                    }
                }
                return false;
            },
            _execute: function(statement, callback, async, format){
                var parameter = this.parseParameter(statement.parameter, format);
                var filterList = this.parseFilter(statement.filter, parameter, format);
                var obj = {
                    "filterList": filterList,
                    "parameter" : parameter
                };
                return MWF.Actions.load("x_query_assemble_surface").StatementAction.executeV2(
                    statement.name, statement.mode || "data", statement.page || 1, statement.pageSize || 20, obj,
                    function (json) {
                        if (callback) callback(json);
                        return json;
                    }, null, async);
            },
            parseFilter: function (filter, parameter, format) {
                if (typeOf(filter) !== "array") return [];
                if( !parameter )parameter = {};
                var filterList = [];
                (filter || []).each(function (d) {
                    if( !d.logic )d.logic = "and";

                    //var parameterName = d.path.replace(/\./g, "_");
                    var pName = d.path.replace(/\./g, "_");

                    var parameterName = pName;
                    var suffix = 1;
                    while( parameter[parameterName] ){
                        parameterName = pName + "_" + suffix;
                        suffix++;
                    }
                    var value = d.value;
                    if (d.comparison === "like" || d.comparison === "notLike") {
                        if (value.substr(0, 1) !== "%") value = "%" + value;
                        if (value.substr(value.length - 1, 1) !== "%") value = value + "%";
                        parameter[parameterName] = value; //"%"+value+"%";
                    } else {
                        if( ["sql", "sqlScript"].contains(format) ) {
                            if (d.formatType === "numberValue") {
                                value = parseFloat(value);
                            }
                        }else{
                            if (d.formatType === "dateTimeValue" || d.formatType === "datetimeValue") {
                                value = "{ts '" + value + "'}"
                            } else if (d.formatType === "dateValue") {
                                value = "{d '" + value + "'}"
                            } else if (d.formatType === "timeValue") {
                                value = "{t '" + value + "'}"
                            } else if (d.formatType === "numberValue") {
                                value = parseFloat(value);
                            }
                        }
                        parameter[parameterName] = value;
                    }
                    d.value = parameterName;

                    filterList.push(d);
                }.bind(this));
                return filterList;
            },
            parseParameter : function( obj, format ){
                if( typeOf(obj) !== "object" )return {};
                var parameter = {};
                //전달된 매개변수
                for( var p in obj ){
                    var value = obj[p];
                    if( typeOf( value ) === "date" ){
                        if( ["sql", "sqlScript"].contains(format) ){
                            value = value.format("db");
                        }else{
                            value = "{ts '"+value.format("db")+"'}"
                        }
                    }
                    parameter[ p ] = value;
                }
                return parameter;
            },
            "select": function(statement, okCallback, dialogOptions, statementOptions, loadedCallback){
                if( statement.name || statement.statementName || statement.statementId || statement.statement ) {
                    var statementJson = {
                        "application": statement.application || _form.json.application,
                        "statementName": statement.statementName || statement.name || statement.statement || "",
                        "isTitle": typeOf(statement.isTitle) === 'string' ? statement.isTitle : ((statement.isTitle === false) ? "no" : "yes"),
                        "select": typeOf(statement.select) === 'string' ? statement.select : ((statement.isMulti === false) ? "single" : "multi"),
                        "filter": statement.filter,
                        "parameter": statement.parameter
                    };
                    if (statement.name)statementJson.statementId = statement.statementId;
                    if (statement.statementId) statementJson.statementId = statement.statementId;
                    // if (statement.hasOwnProperty('titleStyles')) statementJson.titleStyles = statement.titleStyles;
                    // if (statement.hasOwnProperty('itemStyles')) statementJson.itemStyles = statement.itemStyles;
                    // if (statement.hasOwnProperty('isExpand')) statementJson.isExpand = statement.isExpand;
                    // if (statement.hasOwnProperty('showActionbar')) statementJson.showActionbar = statement.showActionbar;
                    // if (statement.hasOwnProperty('defaultSelectedScript')) statementJson.defaultSelectedScript = statement.defaultSelectedScript;
                    // if (statement.hasOwnProperty('selectedAbleScript')) statementJson.selectedAbleScript = statement.selectedAbleScript;
                    for (var key in statement) {
                        if (!statementJson.hasOwnProperty(key)) {
                            statementJson[key] = statement[key];
                        }
                    }
                    //if (layout.mobile && o2.version.dev === 10) {
                    var styleType = _form.json?.formStyleType || _form.viewJson?.viewStyleType;
                    if (layout.mobile && styleType === 'v10') {
                        selectStatementMobile(statementJson, okCallback, dialogOptions, statementOptions, loadedCallback);
                    } else {
                        selectStatementPc(statementJson, okCallback, dialogOptions, statementOptions, loadedCallback);
                    }
                }
            }
        };


        this.importer = {
            "upload": function (options, callback, async) {
                MWF.xDesktop.requireApp("query.Query", "Importer", function () {
                    var importer = new MWF.xApplication.query.Query.Importer(_form.app.content, options, {}, _form.app, _form.Macro);
                    importer.addEvent("afterImport", function (data) {
                        if(callback)callback(data);
                    });
                    importer.load();
                }.bind(this));
            },
            "downloadTemplate": function (options, fileName, callback) {
                MWF.xDesktop.requireApp("query.Query", "Importer", function () {
                    var importer = new MWF.xApplication.query.Query.Importer(_form.app.content, options, {}, _form.app, _form.Macro);
                    importer.downloadTemplate(fileName, callback);
                }.bind(this));
            }
        };

        //include 스크립트 참조
        //optionsOrName : {
        //  type : "", 기본값 portal, portal  process  cms 가능
        //  application : "", 포털/프로세스/CMS의 이름/별칭/id, 기본값은 현재 앱
        //  name : "" // 스크립트 이름/별칭/id
        //}
        //또는 name: "" // 스크립트 이름/별칭/id
        // if( !window.includedScripts ){
        //     var includedScripts = window.includedScripts = [];
        // }else{
        //     var includedScripts = window.includedScripts;
        // }
        var _getScriptAction = function ( type ){
            var scriptAction;
            switch (type) {
                case "portal" :
                    if (this.scriptActionPortal) {
                        scriptAction = this.scriptActionPortal;
                    } else {
                        MWF.require("MWF.xScript.Actions.PortalScriptActions", null, false);
                        scriptAction = this.scriptActionPortal = new MWF.xScript.Actions.PortalScriptActions();
                    }
                    break;
                case "process" :
                    if (this.scriptActionProcess) {
                        scriptAction = this.scriptActionProcess;
                    } else {
                        MWF.require("MWF.xScript.Actions.ScriptActions", null, false);
                        scriptAction = this.scriptActionProcess = new MWF.xScript.Actions.ScriptActions();
                    }
                    break;
                case "cms" :
                    if (this.scriptActionCMS) {
                        scriptAction = this.scriptActionCMS;
                    } else {
                        MWF.require("MWF.xScript.Actions.CMSScriptActions", null, false);
                        scriptAction = this.scriptActionCMS = new MWF.xScript.Actions.CMSScriptActions();
                    }
                    break;
                case "service" :
                    if (this.scriptActionService) {
                        scriptAction = this.scriptActionService;
                    } else {
                        MWF.require("MWF.xScript.Actions.ServiceScriptActions", null, false);
                        scriptAction = this.scriptActionService = new MWF.xScript.Actions.ServiceScriptActions();
                    }
                    break;
            }
            return scriptAction;
        }


        //캐시 이름, 별칭, id
        var _parseScriptImportList = function (json, type){
            var includedScripts = [];
            var importedList = json.data.importedList || [];
            importedList.each(function (flag) {
                if (type === "portal") {
                    includedScripts.push(type + "-" + json.data.portal + "-" + flag);
                    if (json.data.portalName) includedScripts.push(type + "-" + json.data.portalName + "-" + flag);
                    if (json.data.portalAlias) includedScripts.push(type + "-" + json.data.portalAlias + "-" + flag);
                } else if (type === "cms") {
                    includedScripts.push(type + "-" + json.data.appId + "-" + flag);
                    if (json.data.appName) includedScripts.push(type + "-" + json.data.appName + "-" + flag);
                    if (json.data.appAlias) includedScripts.push(type + "-" + json.data.appAlias + "-" + flag);
                } else if (type === "process") {
                    includedScripts.push(type + "-" + json.data.application + "-" + flag);
                    if (json.data.appName) includedScripts.push(type + "-" + json.data.appName + "-" + flag);
                    if (json.data.appAlias) includedScripts.push(type + "-" + json.data.appAlias + "-" + flag);
                }else if (type === "service") {
                    includedScripts.push(type + "-" + flag);
                }
            });
            return includedScripts.concat(importedList);
        }

        var includedScripts = [];
        var _includeSingle = function (optionsOrName, callback, async) {
            var options = optionsOrName;
            if (typeOf(options) === "string") {
                options = {name: options};
            }
            var name = options.name;
            var type;
            if( options.type === "service" ){
                type = options.type;
            }else{
                type = (options.type && options.application) ? options.type : "portal";
            }
            var application = options.application || _form.json.application;
            var key = type + "-" + application + "-" + name;
            if( type === "service" ){
                key = type + "-" + name;
            }

            //js는 이미 로드했으면 다시 로드하지 않음
            if (includedScripts.indexOf(key) > -1) {
                if (callback) callback.apply(this);
                return;
            }

            var successCallback = function (json) {
                if (json.data) {
                    includedScripts.push(key);
                    includedScripts = includedScripts.concat( _parseScriptImportList(json, type) );
                    if( (options.enableAnonymous || options.anonymous ) && type === "cms" ){
                        MWF.CMSMacro.exec(json.data.text, this)
                    }else{
                        MWF.Macro.exec(json.data.text, this);
                    }
                    if (callback) callback.apply(this);
                } else {
                    if (callback) callback.apply(this);
                }
            }.bind(this);

            if (( options.enableAnonymous || options.anonymous ) && type === "cms") {
                o2.Actions.load("x_cms_assemble_control").ScriptAnonymousAction.getWithAppWithName(application, name, successCallback, null, !!async);
            } else {
                var scriptAction = _getScriptAction.call(this, type);
                if( type === "service" ){
                    scriptAction.getScriptByName(name, includedScripts, successCallback, null, !!async);
                }else{
                    scriptAction.getScriptByName(application, name, includedScripts, successCallback, null, !!async);
                }
            }
        };
        this.include = function (optionsOrName, callback, async) {
            if (o2.typeOf(optionsOrName) === "array") {
                if (!!async) {
                    var count = optionsOrName.length;
                    var loaded = 0;
                    optionsOrName.each(function (option) {
                        _includeSingle.apply(this, [option, function () {
                            loaded++;
                            if (loaded >= count) if (callback) callback.apply(this);
                        }.bind(this), true]);
                    }.bind(this));

                } else {
                    optionsOrName.each(function (option) {
                        _includeSingle.apply(this, [option]);
                    }.bind(this));
                    if (callback) callback.apply(this);
                }
            } else {
                _includeSingle.apply(this, [optionsOrName, callback, async])
            }
        };

        var includedSourceMap = {};
        var _includeSource = function (optionsOrName, callback, async, fileType) {
            var options = typeOf(optionsOrName) === "string" ? {name: optionsOrName} : optionsOrName;
            var name = options.name;
            var type = options.type === "service" ? options.type : ((options.type && options.application) ? options.type : "portal");
            var application = options.application || _form.json.application;
            var key = type === "service" ? (type + "-" + name) : (type + "-" + application + "-" + name);
            var data, result;
            if( includedSourceMap[key] ){
                data = includedSourceMap[key];
                if(callback)callback( data.text );
                return !!async ? Promise.resolve( data.text ) : data.text;
            }
            var successCallback = function (json) {
                if (json.data) {
                    var includeds = [key];
                    includeds = includeds.concat( _parseScriptImportList(json, type) );
                    includeds.each(function(k){
                        includedSourceMap[k] = json.data;
                    })
                    result = json.data.text;
                    if( fileType === 'json' ){
                        result = JSON.parse(result);
                    }
                    if (callback) callback.call(this, result);
                } else {
                    result = '';
                    if (callback) callback.call(this, '');
                }
                return result;
            }.bind(this);
            var p;
            if (( options.enableAnonymous || options.anonymous ) && type === "cms") {
                p = o2.Actions.load("x_cms_assemble_control").ScriptAnonymousAction.getWithAppWithName(application, name, !!async ? null : successCallback, null, !!async);
            } else {
                var scriptAction = _getScriptAction.call(this, type);
                if( type === "service" ){
                    p = scriptAction.getScriptByName(name, includedScripts, successCallback, !!async ? null : successCallback, null, !!async);
                }else{
                    p = scriptAction.getScriptByName(application, name, includedScripts, !!async ? null : successCallback, null, !!async);
                }
            }
            return !!async ? p.then( successCallback ) : result;
        };
        this.includeHtml = function (optionsOrName, callback, async){
            return _includeSource.apply(this, [optionsOrName, callback, async!==false, 'html'])
        };
        this.includeJson = function (optionsOrName, callback, async){
            return _includeSource.apply(this, [optionsOrName, callback, async!==false, 'json'])
        };
        this.includeCss = function (optionsOrName, callback, async){
            return _includeSource.apply(this, [optionsOrName, callback, async!==false, 'css']);
        };

        this.define = function (name, fun, overwrite) {
            var over = true;
            if (overwrite === false) over = false;
            var o = {};
            o[name] = {"value": fun, "configurable": over};
            MWF.defineProperties(this, o);
        }.bind(this);

        this.exec = function(text, bind, arg=null, throwError=true){
            const b = bind || globalThis;
            const p = (arg) ? Object.values(arg) : [];
            const k = (arg) ? Object.keys(arg).join(',') : '';
            try {
                return Function('return function('+k+'){' + text + '}')().apply(b, p);
            }catch(e){
                if (throwError) throw e;
                console.error(e);
                return '';
            }
        }

        //프런트엔드 이벤트에 비동기 호출이 있고, 비동기 호출이 끝난 뒤 페이지 로드를 계속하려면,
        //호출 전에 var resolve = this.wait(); 실행
        //비동기 호출이 끝난 뒤 resolve.cb(); 실행
        //현재는 양식의 queryload 이벤트만 이 방법을 지원함.
        this.wait = function () {
            resolve = {};
            var setResolve = function (callback) {
                resolve.cb = callback;
            }.bind(this);
            this.target.event_resolve = setResolve;
            return resolve;
        };
        //this.wait와 함께 사용,
        //비동기가 없으면 resolve.cb 메서드가 없으므로,
        //콜백에서 this.goon();을 사용해 양식 로드를 계속함
        this.goon = function () {
            this.target.event_resolve = null;
        };

        this.invoke = function(name, data){
            return this.Actions.load('x_program_center').InvokeAction.execute(name, data);
        }
        
        //프런트엔드 객체 전용-----------------------------------------
        //form
        /**
         * page 객체는 포털 페이지에서 사용할 수 있습니다. 많은 메서드가 form과 유사합니다.<b>(프런트엔드 스크립트 전용)</b><br/>
         * @module page
         * @o2cn 포털 페이지
         * @o2category web
         * @o2range {Portal}
         * @o2ordernumber 50
         * @o2syntax
         * //포털 양식에서 this를 통해 page 객체를 가져올 수 있습니다. 예:
         * var page = this.page;
         */
        this.page = this.form = {
            /** 현재 포털의 지정 페이지로 이동.<b>(포털 페이지 스크립트 전용)</b>
             * @method toPage
             * @static
             * @param {String} name - 이동할 페이지 이름
             * @param {Object} [par] - 열리는 페이지에 전달할 데이터. 열린 페이지에서 this.page.parameters로 조회 가능
             * @param {Boolean} [nohis] - 페이지 이동 시 History에 기록 상태를 추가하지 않음, 기본값 false
             * @o2syntax
             * //현재 포털의 지정 페이지로 이동.
             * this.page.toPage( name, par, nohis );
             * @example
             * this.page.toPage("목록 페이지", {"key": "발신 문서 목록"});//“목록 페이지”를 열고 json 데이터 하나를 전달.
             *
             * //목록 페이지에서 this.page.parameters로 전달된 데이터 조회.
             * var key = this.page.parameters.key; //key="발신 문서 목록"
             */
            "toPage": function (name, par, nohis) {
                _form.app.toPage(name, par, nohis);
            },

            /** 지정 포털 페이지로 이동.<b>(포털 페이지 스크립트 전용)</b>
             * @method toPortal
             * @static
             * @param {String} portal - 이동할 포털 이름.
             * @param {String} [page] - 열 포털의 페이지 이름. 비어 있으면 지정 포털의 기본 첫 페이지를 염.
             * @param {String} [par] - 열린 페이지에서 this.page.parameters로 조회 가능.
             * @o2syntax
             * this.page.toPortal( portal, page, par );
             * @example
             * this.page.toPortal("공문 포털", "목록 페이지", {"key": "발신 문서 목록"});//“공문 포털”의 “목록 페이지”를 열고 json 데이터 하나를 전달.
             *
             * //목록 페이지에서 this.page.parameters로 전달된 데이터 조회.
             * var key = this.page.parameters.key; //key="발신 문서 목록"
             */
            "toPortal": function (portal, page, par) {
                _form.app.toPortal(portal, page, par);
            },
            /**현재 페이지 기본 정보 조회
             * @method getInfor
             * @static
             * @see module:form.getInfor
             */
            "getInfor": function () {
                return ev.pageInfor;
            },
            "infor": ev.pageInfor,
            /**현재 페이지를 연 component 객체 조회.
             * @method getApp
             * @static
             * @see module:form.getApp
             */
            "getApp": function () {
                return _form.app;
            },
            "app": _form.app,
            /**page에 대응하는 DOM 객체 조회.
             * @method node
             * @static
             * @see module:form.node
             */
            "node": function () {
                return _form.node;
            },
            //"readonly": _form.options.readonly,
            /**페이지 요소 객체 조회.
             * @method get
             * @static
             * @see module:form.get
             */
            "get": function(name,subformName ){
                if( !_form.all )return null;
                if( subformName ){
                    if( _form.all[subformName +"_"+ name] )return _form.all[subformName +"_"+ name];
                    return _form.all[name];
                }else{
                    return _form.all[name];
                }
                // return (_form.all) ? _form.all[name] : null;
            },

            /**지정 위젯 요소 객체 조회.<br/>
             * @method getWidgetModule
             * @static
             * @param {String} widgetId  - 메인 페이지에 위젯을 삽입할 때 사용한 식별자.
             * @param {String} fieldId  - 위젯 내 컴포넌트 식별자.
             * @return {FormComponent} 이 문서의 Classes 탐색에서 FormComponents를 참고하세요.
             * @see module:form.get
             * @o2syntax
             * this.page.getWidgetModule( widgetId, fieldId );
             * @example
             * <caption>
             * 1. 설계 요소 subject를 포함한 위젯을 설계.<br/>
             * 2. 메인 페이지에 1단계에서 만든 위젯을 두 번 삽입, 식별자는 widget_1, widget_2.
             * </caption>
             * var module = this.page.getWidgetModule( "widget_1", "subject"); //위젯 widget_1의 subject 컴포넌트
             * var data2 = this.page.getWidgetModule( "widget_2", "subject").getData(); //위젯 widget_2의 subject 컴포넌트 값
             */
            "getWidgetModule": function (widget, moduleName) {
                if (!_form.widgetModules || !_form.widgetModules[widget]) return null;
                var module = _form.widgetModules[widget][moduleName];
                return module || null;
            },
            /**페이지의 입력 가능한 필드 요소 객체 조회
             * @method getField
             * @static
             * @see module:form.getField
             */
            "getField": function (name) {
                return _forms[name];
            },
            "getAction": function () {
                return _form.workAction
            },
            "getDesktop": function () {
                return _form.app.desktop
            },

            "getData": function () {
                return new MWF.xScript.JSONData(_form.getData());
            },
            //"save": function(callback){_form.saveWork(callback);},

            /**현재 페이지 닫기
             * @method close
             * @static
             * @see module:form.close
             */
            "close": function () {
                _form.closeWork();
            },

            "print": function (application, form) {
                _form.printWork(application, form);
            },

            /**확인 창 표시
             * @method confirm
             * @static
             * @see module:form.confirm
             */
            "confirm": function (type, title, text, width, height, ok, cancel, callback, mask, style) {
                // var p = MWF.getCenter({"x": width, "y": height});
                // e = {"event": {"clientX": p.x,"x": p.x,"clientY": p.y,"y": p.y}};
                // _form.confirm(type, e, title, text, width, height, ok, cancel, callback, mask, style);
                if ((arguments.length <= 1) || o2.typeOf(arguments[1]) === "string") {
                    var p = MWF.getCenter({"x": width, "y": height});
                    e = {"event": {"clientX": p.x, "x": p.x, "clientY": p.y, "y": p.y}};
                    _form.confirm(type, e, title, text, width, height, ok, cancel, callback, mask, style);
                } else {
                    e = (arguments.length > 1) ? arguments[1] : null;
                    title = (arguments.length > 2) ? arguments[2] : null;
                    text = (arguments.length > 3) ? arguments[3] : null;
                    width = (arguments.length > 4) ? arguments[4] : null;
                    height = (arguments.length > 5) ? arguments[5] : null;
                    ok = (arguments.length > 6) ? arguments[6] : null;
                    cancel = (arguments.length > 7) ? arguments[7] : null;
                    callback = (arguments.length > 8) ? arguments[8] : null;
                    mask = (arguments.length > 9) ? arguments[9] : null;
                    style = (arguments.length > 10) ? arguments[10] : null;
                    _form.confirm(type, e, title, text, width, height, ok, cancel, callback, mask, style);
                }
            },
            /**닫기 버튼이 있는 정보 창 표시
             * @method alert
             * @static
             * @see module:form.alert
             */
            "alert": function (type, title, text, width, height) {
                _form.alert(type, title, text, width, height);
            },

            /**정보 창 표시
             * @method notice
             * @static
             * @see module:form.notice
             */
            "notice": function (content, type, target, where, offset, option) {
                _form.notice(content, type, target, where, offset, option);
            },

            /**대화상자 열기
             * @method dialog
             * @static
             * @see module:form.dialog
             */
            "dialog": function ( options ) {
                return _form.dialog( options );
            },

            /**사용자 조직 선택 화면 열기
             * @method selectOrg
             * @static
             * @see module:form.selectOrg
             */
            "selectOrg": function ( container, options,  delayLoad) {
                if( !container )container = _form.app.content;
                return new MWF.O2Selector(container, options, delayLoad);
            },

            /**페이지에 이벤트 추가.
             * @method addEvent
             * @static
             * @see module:form.addEvent
             */
            "addEvent": function (e, f) {
                _form.addEvent(e, f);
            },

            "openWindow": function (form, app) {
                _form.openWindow(form, app);
            },

            /**진행 중이거나 완료된 프로세스 인스턴스 열기
             * @method openWork
             * @static
             * @see module:form.openWork
             */
            "openWork": function (id, completedId, title, options) {
                var op = options || {};
                op.workId = id;
                op.workCompletedId = completedId;
                op.docTitle = title;
                op.appId = "process.Work" + (op.workId || op.workCompletedId);
                return layout.desktop.openApplication(this.event, "process.Work", op);
            },
            /**프로세스 jobId로 업무 문서 열기
             * @method openJob
             * @static
             * @see module:form.openJob
             */
            "openJob": function (id, choice, options, callback) {
                var workData = null, handel;
                o2.Actions.get("x_processplatform_assemble_surface").listWorkByJob(id, function(json){
                    if (json.data) workData = json.data;
                }.bind(this), null, false);

                if( !layout.inBrowser && o2.typeOf(callback) === "function" ){
                    if( !options )options = {};
                    var queryLoad = options.onQueryLoad;
                    options.onQueryLoad = function () {
                        if( o2.typeOf(queryLoad) === "function" )queryLoad.call(this);
                        callback(this);
                    }
                }

                runCallback = function ( handel ) {
                    if( o2.typeOf(callback) === "function" ) {
                        if (layout.inBrowser) {
                            callback(handel);
                        } else if (options && options.appId) {
                            if (layout.desktop && layout.desktop.apps && layout.desktop.apps[options.appId]) {
                                callback(layout.desktop.apps[options.appId], true);
                            }else{
                                callback(handel, false);
                            }
                        }else{
                            callback(handel, false);
                        }
                    }
                };

                if (workData){
                    var len = workData.workList.length + workData.workCompletedList.length;
                    if (len){
                        if (len>1 && choice){
                            var node = new Element("div", {"styles": {"padding": "20px", "width": "500px"}}).inject(_form.node || _form.app.contentNode);
                            workData.workList.each(function(work){
                                var workNode = new Element("div", {
                                    "styles": {
                                        "background": "#ffffff",
                                        "border-radius": "10px",
                                        "clear": "both",
                                        "margin-bottom": "10px",
                                        "height": "40px",
                                        "padding": "10px 10px"
                                    }
                                }).inject(node);
                                var html = "<div style='height: 40px; width: 40px; float: left; background: url(../x_component_process_Xform/$Form/default/icon/work.png) no-repeat center center'></div>" +
                                    "<div style='height: 40px; width: 40px; float: right'><div class='MWFAction' style='height: 20px; width: 40px; margin-top: 10px; border: 1px solid #999999; border-radius: 5px;text-align: center; cursor: pointer'>"+o2.LP.widget.open+"</div></div>"+
                                    "<div style='height: 20px; line-height: 20px; margin: 0px 40px'>"+work.title+"</div>" +
                                    "<div style='margin: 0px 40px'><div style='color:#999999; float: left; margin-right: 10px'>"+work.activityName+"</div>" +
                                    "<div style='color:#999999; float: left; margin-right: 10px'>"+work.activityArrivedTime+"</div>" +
                                    "<div style='color:#999999; float: left; margin-right: 10px'>"+(work.manualTaskIdentityText || "")+"</div></div>";
                                workNode.set("html", html);
                                var action = workNode.getElement(".MWFAction");
                                action.store("work", work);
                                action.addEvent("click", function(e){
                                    var work = e.target.retrieve("work");
                                    if (work){
                                        handel =  this.openWork(work.id, null, work.title, options);
                                        runCallback( handel );
                                    }
                                    dlg.close();
                                }.bind(this));

                            }.bind(this));
                            workData.workCompletedList.each(function(work){
                                var workNode = new Element("div", {
                                    "styles": {
                                        "background": "#ffffff",
                                        "border-radius": "10px",
                                        "clear": "both",
                                        "margin-bottom": "10px",
                                        "height": "40px",
                                        "padding": "10px 10px"
                                    }
                                }).inject(node);
                                var html = "<div style='height: 40px; width: 40px; float: left; background: url(../x_component_process_Xform/$Form/default/icon/work.png) no-repeat center center'></div>" +
                                    "<div style='height: 40px; width: 40px; float: right'><div class='MWFAction' style='height: 20px; width: 40px; margin-top: 10px; border: 1px solid #999999; border-radius: 5px;text-align: center; cursor: pointer'>"+o2.LP.widget.open+"</div></div>"+
                                    "<div style='height: 20px; line-height: 20px; margin: 0px 40px'>"+work.title+"</div>" +
                                    "<div style='margin: 0px 40px'><div style='color:#999999; float: left; margin-right: 10px'>"+o2.LP.widget.workcompleted+"</div>" +
                                    "<div style='color:#999999; float: left; margin-right: 10px'>"+work.completedTime+"</div>";
                                workNode.set("html", html);
                                var action = workNode.getElement(".MWFAction");
                                action.store("work", work);
                                action.addEvent("click", function(e){
                                    var work = e.target.retrieve("work");
                                    if (work){
                                        handel =  this.openWork(null, work.id, work.title, options);
                                        runCallback( handel );
                                    }
                                    dlg.close();
                                }.bind(this));

                            }.bind(this));
                            var height = node.getSize().y+20;
                            if (height>600) height = 600;

                            var dlg = o2.DL.open({
                                "title": o2.LP.widget.choiceWork,
                                "style" : "user",
                                "isResize": false,
                                "content": node,
                                "buttonList": [
                                    {
                                        "type" : "cancel",
                                        "text": o2.LP.widget.close,
                                        "action": function(){dlg.close();}
                                    }
                                ]
                            });
                        }else{
                            if (workData.workList.length){
                                var work =  workData.workList[0];
                                handel = this.openWork(work.id, null, work.title, options);
                                runCallback( handel );
                                return handel;
                            }else{
                                var work =  workData.workCompletedList[0];
                                handel = this.openWork(null, work.id, work.title, options);
                                runCallback( handel );
                                return handel;
                            }
                        }
                    }else{
                        _form.notice(o2.LP.widget.noWork, 'warn');
                        runCallback(new Error("Can't open this Job", {
                            cause: workData
                        }));
                    }
                }else{
                    _form.notice(o2.LP.widget.noWork, 'warn');
                    runCallback(new Error("Can't open this Job", {
                        cause: workData
                    }));
                }
            },
            /**콘텐츠 관리 문서 열기
             * @method openDocument
             * @static
             * @see module:form.openDocument
             */
            "openDocument": function (id, title, options) {
                var op = options || {};
                op.documentId = id;
                op.docTitle = title || "";
                op.appId = (op.appId) || ("cms.Document"+id);
                if( op.onPostPublish ){
                    op.postPublish = op.onPostPublish;
                    delete op.onPostPublish;
                }
                if( op.onAfterPublish ){
                    op.afterPublish = op.onAfterPublish;
                    delete op.onAfterPublish;
                }
                if( op.onAfterSave ){
                    op.afterSave = op.onAfterSave;
                    delete op.onAfterSave;
                }
                if( op.onBeforeClose ){
                    op.beforeClose = op.onBeforeClose;
                    delete op.onBeforeClose;
                }
                if( op.onPostDelete ){
                    op.postDelete = op.onPostDelete;
                    delete op.onPostDelete;
                }
                return layout.desktop.openApplication(this.event, "cms.Document", op);
            },
            /**포털 페이지 열기
             * @method openPortal
             * @static
             * @see module:form.openPortal
             */
            "openPortal": function (name, page, par) {
                var action = MWF.Actions.get("x_portal_assemble_surface");
                action.getApplication(name, function (json) {
                    if (json.data) {
                        if (page) {
                            action.getPageByName(page, json.data.id, function (pageJson) {
                                var pageId = (pageJson.data) ? pageJson.data.id : "";
                                layout.desktop.openApplication(null, "portal.Portal", {
                                    "portalId": json.data.id,
                                    "pageId": pageId,
                                    "parameters": par,
                                    "appId": (par && par.appId) || ("portal.Portal" + json.data.id + pageId)
                                })
                            });
                        } else {
                            layout.desktop.openApplication(null, "portal.Portal", {
                                "portalId": json.data.id,
                                "parameters": par,
                                "appId": (par && par.appId) || ("portal.Portal" + json.data.id)
                            })
                        }
                    }

                });
            },
            /**포털 페이지 열기
             * @method loadPortal
             * @static
             * @see module:form.loadPortal
             */
            "loadPortal": function (content, portal, page, data, par) {
                const app = new MWF.xApplication.portal.Portal.Main(layout.desktop, {
                    portalId: portal,
                    pageId: page,
                    data: data,
                    parameters: par
                });
                app.viewMode="Default";
                app.windowNode = content;
                app.setCurrent = function(){
                    this.window.setCurrent();
                }
                app.setUncurrent = function(){
                    this.window.setUncurrent();
                }
                app.close = function(){
                    this.fireAppEvent("queryClose");
                    this.window.close(function () {
                        this.window = null;
                        this.taskitem = null;
                        this.fireAppEvent("postClose");
                        o2.release(this);
                    }.bind(this));
                }
                app.load(true, content);

                return app;
            },
            /**콘텐츠 관리 게시판(앱) 열기
             * @method openCMS
             * @static
             * @see module:form.openCMS
             */
            "openCMS": function (name) {
                var action = MWF.Actions.get("x_cms_assemble_control");
                action.getColumn(name, function (json) {
                    if (json.data) {
                        layout.desktop.openApplication(null, "cms.Module", {
                            "columnId": json.data.id,
                            "appId": "cms.Module" + json.data.id
                        });
                    }
                });
            },
            /**프로세스 앱 열기
             * @method openProcess
             * @static
             * @see module:form.openProcess
             */
            "openProcess": function (name) {
                var action = MWF.Actions.get("x_processplatform_assemble_surface");
                action.getApplication(name, function (json) {
                    if (json.data) {
                        layout.desktop.openApplication(null, "process.Application", {
                            "id": json.data.id,
                            "appId": "process.Application" + json.data.id
                        });
                    }
                });
            },
            /**임의의 component 앱 열기
             * @method openApplication
             * @static
             * @see module:form.openApplication
             */
            "openApplication": function (name, options, status) {
                return layout.desktop.openApplication(null, name, options, status);
            },
            /**콘텐츠 관리 문서 생성
             * @method createDocument
             * @static
             * @see module:form.createDocument
             */
            "createDocument": function (columnOrOptions, category, data, identity, callback, target, latest, selectColumnEnable, ignoreTitle, restrictToColumn) {
                var column = columnOrOptions;
                var onAfterPublish, onPostPublish;
                if (typeOf(columnOrOptions) == "object") {
                    column = columnOrOptions.column;
                    category = columnOrOptions.category;
                    data = columnOrOptions.data;
                    identity = columnOrOptions.identity;
                    callback = columnOrOptions.callback;
                    target = columnOrOptions.target;
                    latest = columnOrOptions.latest;
                    selectColumnEnable = columnOrOptions.selectColumnEnable;
                    ignoreTitle = columnOrOptions.ignoreTitle;
                    restrictToColumn = columnOrOptions.restrictToColumn;
                    onAfterPublish = columnOrOptions.onAfterPublish;
                    onPostPublish = columnOrOptions.onPostPublish;
                }

                if (target) {
                    if (layout.app && layout.app.inBrowser) {
                        layout.app.content.empty();
                        layout.app = null;
                    }
                }

                MWF.xDesktop.requireApp("cms.Index", "Newer", function () {
                    var starter = new MWF.xApplication.cms.Index.Newer(null, null, _form.app, null, {
                        "documentData": data,
                        "identity": identity,

                        "ignoreTitle": ignoreTitle === true,
                        "ignoreDrafted": latest === false,
                        "selectColumnEnable": !category || selectColumnEnable === true,
                        "restrictToColumn": restrictToColumn === true || (!!category && selectColumnEnable !== true),

                        "categoryFlag": category, //category id or name
                        "columnFlag": column, //column id or name,
                        "onStarted": function (documentId, data, windowHandle) {
                            if (callback) callback(documentId, data, windowHandle);
                        },
                        "onPostPublish": function () {
                            if (onPostPublish) onPostPublish();
                        },
                        "onAfterPublish": function () {
                            if (onAfterPublish) onAfterPublish();
                        }
                    });
                    starter.load();
                })
            },
            /**프로세스 인스턴스 시작
             * @method startProcess
             * @static
             * @see module:form.startProcess
             */
            "startProcess": function (app, process, data, identity, callback, target, latest, afterCreated, skipDraftCheck) {
                if (arguments.length > 2) {
                    for (var i = 2; i < arguments.length; i++) {
                        if (typeOf(arguments[i]) == "boolean") {
                            target = arguments[i];
                            break;
                        }
                    }
                }

                if (target) {
                    if (layout.app && layout.app.inBrowser) {
                        //layout.app.content.empty();
                        layout.app.$openWithSelf = true;
                    }
                }
                if (!app || !process){
                    var cmpt = this.getApp();
                    o2.requireApp([["process.TaskCenter", "lp."+o2.language], ["process.TaskCenter", ""]],"", function(){
                        var obj = {
                            "lp": o2.xApplication.process.TaskCenter.LP,
                            "content": cmpt.content,
                            "addEvent": function(type, fun){
                                cmpt.addEvent(type, fun);
                            },
                            "getAction": function (callback) {
                                if (!this.action) {
                                    this.action = o2.Actions.get("x_processplatform_assemble_surface");
                                    if (callback) callback();
                                } else {
                                    if (callback) callback();
                                }
                            },
                            "desktop": layout.desktop,
                            "refreshAll": function(){},
                            "notice": cmpt.notice,
                        }
                        o2.JSON.get("../x_component_process_TaskCenter/$Main/default/css.wcss", function(data){
                            obj.css = data;
                        }, false);

                        if (!cmpt.processStarter) cmpt.processStarter = new o2.xApplication.process.TaskCenter.Starter(obj);
                        cmpt.processStarter.load({
                            "appFlag": app
                        });
                    }, true, true);
                    return "";
                }
                MWF.xDesktop.requireApp("process.TaskCenter", "ProcessStarter", null, false);
                var action = MWF.Actions.get("x_processplatform_assemble_surface").getProcessByName(process, app, function (json) {
                    if (json.data) {
                    var attachmentList = [];
                    var correlationTargetList = [];
                    if( data && data.$attachmentList && o2.typeOf(data.$attachmentList) === 'array' ){
                        attachmentList = data.$attachmentList.filter(function(d){
                            return !!d.copyFrom;
                        });
                        data.$attachmentList = data.$attachmentList.filter(function(d){
                            return !d.copyFrom;
                        });
                        if( !data.$attachmentList.length )delete data.$attachmentList;
                    }
                    if( data && data.$correlationTargetList && o2.typeOf(data.$correlationTargetList) === 'array' ){
                        correlationTargetList = data.$correlationTargetList;
                        delete data.$correlationTargetList;
                    }
                        var starter = new MWF.xApplication.process.TaskCenter.ProcessStarter(json.data, _form.app, {
                            "workData": data,
                            "attachmentList": attachmentList,
                            "correlationTargetList": correlationTargetList,
                            "identity": identity,
                            "latest": latest,
                            "skipDraftCheck": skipDraftCheck,
                            "onStarted": function (data, title, processName) {
                                var application;
                                if (data.work) {
                                    var work = data.work;
                                    var options = {
                                        "draft": work,
                                        "draftData":data.data||{},
                                        "appId": "process.Work" + (new o2.widget.UUID).toString(),
                                        "desktopReload": false
                                    };
                                    if( !layout.inBrowser && afterCreated )options.onPostLoadForm = afterCreated;
                                    application = layout.desktop.openApplication(null, "process.Work", options);
                                } else {
                                    var currentTask = [];
                                    data.each(function (work) {
                                        if (work.currentTaskIndex != -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
                                    }.bind(this));

                                    if (currentTask.length == 1) {
                                        var options = {"workId": currentTask[0], "appId": currentTask[0]};
                                        if( !layout.inBrowser && afterCreated )options.onPostLoadForm = afterCreated;
                                        application = layout.desktop.openApplication(null, "process.Work", options);
                                    } else {
                                    }
                                }

                                // var currentTask = [];
                                // data.each(function (work) {
                                //     if (work.currentTaskIndex != -1) currentTask.push(work.taskList[work.currentTaskIndex].work);
                                // }.bind(this));
                                //
                                // if (currentTask.length == 1) {
                                //     var options = { "workId": currentTask[0], "appId": currentTask[0] };
                                //     layout.desktop.openApplication(null, "process.Work", options);
                                // } else { }

                                if (callback) callback(data);

                                if(layout.inBrowser && afterCreated){
                                    afterCreated(application)
                                }
                            }.bind(this)
                        });
                        starter.load();
                    }
                });
            },

            /**
             * 열린 페이지의 임의 스크립트에서 전달된 파라미터 조회.
             * @member parameters
             * @static
             * @return {Boolean} 임의 데이터 타입, 전달된 파라미터에 따라 결정.
             * @o2syntax
             * var par = this.page.parameters
             * @example
             * //페이지를 열 때 파라미터 전달:
             * this.form.openPortal(id, "", {"type": "my type"});
             *
             * //열린 페이지의 임의 스크립트에서 parameters 조회 가능:
             * var par = this.page.parameters;
             * //par의 내용: {"type": "my type"}
             */
            "parameters": _form.options.parameters,

            /**
             * 위젯 삽입 시 메인 페이지에서 파라미터를 전달할 수 있습니다. 이 메서드로 해당 위젯 또는 위젯 요소의 스크립트에서 전달된 파라미터를 조회할 수 있습니다.
             * @method getWidgetPrameters
             * @static
             * @return {Object} 임의 데이터 타입, 전달된 파라미터에 따라 결정.
             * @o2syntax
             * var par = this.page.getWidgetPrameters();
             * @example
             * //메인 페이지의 위젯 삽입 위치에서 파라미터 전달:
             * return {"type": "my type"};
             *
             * //해당 위젯 스크립트에서 parameters 조회 가능:
             * var par = this.page.getWidgetPrameters();
             * //par의 내용: {"type": "my type"}
             */
            "getWidgetPrameters": function (target) {
                var currentTarget = target || this.target;
                if (!currentTarget) return null;
                if (!currentTarget.widget) return null;
                if (!this.widgetParameters) return null;
                var pageId = currentTarget.widget.json.id;
                return this.widgetParameters[pageId];
            }.bind(this),

            addRelated: function(path, module){
                if (!_form.relatedModules) _form.relatedModules = {};
                if (!_form.relatedModules[path]) _form.relatedModules[path] = new Set();
                _form.relatedModules[path].add(module);
            }
            //"app": _form.app
        };
        this.form.currentRouteName = _form.json.currentRouteName;
        this.form.opinion = _form.json.opinion;

        this.target = ev.target;
        this.event = ev.event;
        this.status = ev.status;
        this.session = layout.desktop.session;
        this.Actions = o2.Actions;

        this.query = function (option) {
            // options = {
            //      "name": "statementName",
            //      "data": "json data",
            //      "firstResult": 1,
            //      "maxResults": 100,
            //      "success": function(){},
            //      "error": function(){},
            //      "async": true or false, default is true
            // }
            if (option) {
                var json = (option.data) || {};
                if (option.firstResult) json.firstResult = option.firstResult.toInt();
                if (option.maxResults) json.maxResults = option.maxResults.toInt();
                o2.Actions.get("x_query_assemble_surface").executeStatement(option.name, json, success, error, options.async);
            }
        };
        this.Table = MWF.xScript.createTable();
    };
}

if (!MWF.xScript.createTable) MWF.xScript.createTable = function(){
    return function(name){
        this.name = name;
        this.action = o2.Actions.load("x_query_assemble_surface").TableAction;

        this.listRowNext = function(id, count, success, error, async){
            return this.action.listRowNext(this.name, id, count, success, error, async);
        };
        this.listRowPrev = function(id, count, success, error, async){
            return this.action.listRowPrev(this.name, id, count, success, error, async);
        };
        this.listRowSelect = function(where, orderBy, size, success, error, async){
            return this.action.listRowSelect(this.name, {"where": where, "orderBy": orderBy, "size": size || ""}, success, error, async);
        };
        this.listRowSelectWhere = function(where, success, error, async){
            return this.action.listRowSelectWhere(this.name, where, success, error, async);
        };
        this.rowCountWhere = function(where, success, error, async){
            return this.action.rowCountWhere(this.name, where, success, error, async);
        };
        this.deleteRow = function(id, success, error, async){
            return this.action.rowDelete(this.name, id, success, error, async);
        };
        this.deleteAllRow = function(success, error, async){
            return this.action.rowDeleteAll(this.name, success, error, async);
        };
        this.getRow = function(id, success, error, async){
            return this.action.rowGet(this.name, id, success, error, async);
        };
        this.insertRow = function(data, success, error, async){
            return this.action.rowInsert(this.name, data, success, error, async);
        };
        this.addRow = function(data, success, error, async){
            return this.action.rowInsertOne(this.name, data, success, error, async);
        };
        this.updateRow = function(id, data, success, error, async){
            return this.action.rowUpdate(this.name, id, data, success, error, async);
        };
        this.partUpdateRow = function(id, data, success, error, async){
            return this.action.rowPartUpdate(this.name, id, data, success, error, async);
        };
    }
};
if (!MWF.xScript.JSONData) {
    var getArrayJSONData = function (jData, p, _form) {
        return new MWF.xScript.JSONData(jData, function (data, key, _self) {
            var p = { 
                "getKey": function () {
                    return key;
                }, "getParent": function () {
                    return _self;
                }
            };
            while (p && !_form.forms[p.getKey()]) p = p.getParent();
            //if (p) if (p.getKey()) if (_forms[p.getKey()]) _forms[p.getKey()].resetData();
            var k = (p) ? p.getKey() : "";
            if (k) if (_form.forms[k]) if (_form.forms[k].resetData) _form.forms[k].resetData();
            //if(p) if(p.getKey()) if(_forms[p.getKey()]) if(_forms[p.getKey()].render) _forms[p.getKey()].render();
        }, "", p, _form);
    };
    MWF.xScript.JSONData = function (data, callback, key, parent, _form) {
        var getter = function (data, callback, k, _self) {
            return function () {
                var t = typeOf(data[k]);
                if (["array", "object"].indexOf(t) === -1) {
                    return data[k]
                } else {
                    if (t === "array") {
                        if (window.Proxy){
                            var arr = new Proxy(data[k], {
                                get: function(o, k){
                                    return (o2.typeOf(o[k])==="object") ? getArrayJSONData(o[k], _self, _form) : o[k];
                                },
                                set: function(o, k, v){
                                    o[k] = v;
                                    if (callback) callback(o, k, _self);
                                    return true;
                                }
                            });
                            return arr;
                        }else{
                            var arr =[];
                            data[k].forEach(function(d, i){
                                arr.push((o2.typeOf(d)==="object") ? getArrayJSONData(d, _self, _form) : d);
                            });
                            return arr;
                        }
                    } else {
                        return new MWF.xScript.JSONData(data[k], callback, k, _self, _form);
                    }
                    // var obj =
                    // if (t==="array") obj.constructor = Array;
                    // return obj;
                }
                //return (["array","object"].indexOf(typeOf(data[k]))===-1) ? data[k] : new MWF.xScript.JSONData(data[k], callback, k, _self, _form);
            };
        };
        var setter = function (data, callback, k, _self) {
            return function (v) {
                data[k] = v;
                //debugger;
                //this.add(k, v, true);
                if (callback) callback(data, k, _self);
            }
        };
        var define = function () {
            var o = {};
            for (var k in data) o[k] = {
                "configurable": true,
                "enumerable": true,
                "get": getter.apply(this, [data, callback, k, this]),
                "set": setter.apply(this, [data, callback, k, this])
            };
            o["length"] = {
                "get": function () {
                    return Object.keys(data).length;
                }
            };
            o["some"] = {
                "get": function () {
                    return data.some;
                }
            };
            MWF.defineProperties(this, o);

            var methods = {
                "getKey": {
                    "value": function () {
                        return key;
                    }
                },
                "getParent": {
                    "value": function () {
                        return parent;
                    }
                },
                "toString": {
                    "value": function () {
                        return data.toString();
                    }
                },
                "setSection": {
                    "value": function (newKey, newValue) {
                        this.add(newKey, newValue, true);
                        try {
                            var path = [this.getKey()];
                            p = this.getParent();
                            while (p && p.getKey()) {
                                path.unshift(p.getKey());
                                p = p.getParent();
                            }
                            if (path.length) _form.sectionListObj[path.join(".")] = newKey;
                        } catch (e) {

                        }
                    }
                },

                "add": {
                    "value": function (newKey, newValue, overwrite, noreset) {
                        if( newKey.test(/^\d+$/) ){
                            throw new Error("Field name '"+newKey+"' cannot contain only numbers" );
                        }
                        if (arguments.length < 2 || newKey.indexOf("..") === -1) {
                            var flag = true;
                            var type = typeOf(data);
                            if (type === "array") {
                                if (arguments.length < 2) {
                                    data.push(newKey);
                                    newValue = newKey;
                                    newKey = data.length - 1;
                                } else {
                                    if (!newKey && newKey !== 0) {
                                        data.push(newValue);
                                        newKey = data.length - 1;
                                    } else {
                                        if (newKey >= data.length) {
                                            data.push(newValue);
                                            newKey = data.length - 1;
                                        } else {
                                            if (overwrite) data[newKey] = newValue;
                                            newValue = data[newKey];
                                            flag = false;
                                        }
                                    }
                                }
                                if (flag) {
                                    var o = {};
                                    o[newKey] = {
                                        "configurable": true,
                                        "enumerable": true,
                                        "get": getter.apply(this, [data, callback, newKey, this]),
                                        "set": setter.apply(this, [data, callback, newKey, this])
                                    };
                                    MWF.defineProperties(this, o);
                                }
                                if (!noreset) this[newKey] = newValue;
                            } else if (type === "object") {
                                if (!this.hasOwnProperty(newKey)) {
                                    if (!data[newKey] || overwrite) {
                                        data[newKey] = newValue;
                                    }
                                    newValue = data[newKey];

                                    if (flag) {
                                        var o = {};
                                        o[newKey] = {
                                            "configurable": true,
                                            "enumerable": true,
                                            "get": getter.apply(this, [data, callback, newKey, this]),
                                            "set": setter.apply(this, [data, callback, newKey, this])
                                        };
                                        MWF.defineProperties(this, o);
                                    }
                                    if (!noreset) this[newKey] = newValue;
                                } else {
                                    if (!Object.getOwnPropertyDescriptor(this, newKey).get){
                                        var o = {};
                                        o[newKey] = {"configurable": true, "enumerable": true, "get": getter.apply(this, [data, callback, newKey, this]),"set": setter.apply(this, [data, callback, newKey, this])};
                                        MWF.defineProperties(this, o);
                                    }
                                    if (overwrite) {
                                        data[newKey] = newValue;
                                        if (!noreset) this[newKey] = newValue;
                                    }
                                }
                            }
                            return this[newKey];
                        } else {
                            var keys = newKey.split("..");
                            var kk = keys.shift();
                            var d = this.add(kk, {}, false, true);
                            if (keys.length) return d.add(keys.join(".."), newValue, overwrite, noreset);
                            return d;
                        }
                    }
                },
                "check": {
                    "value": function (kk, v) {
                        var value = typeOf( v ) === "null" ? "" : v;
                        this.add(kk, value, false, true);
                    }
                },
                "del": {
                    "value": function (delKey) {
                        if (!this.hasOwnProperty(delKey)) return null;
                        // delete data[delKey];
                        // delete this[delKey];
                        data[delKey] = "";
                        this[delKey] = "";
                        return this;
                    }
                }
            };
            MWF.defineProperties(this, methods);

            //this.getKey = function(){ return key; };
            //this.getParent = function(){ return parent; };
            //this.toString = function() { return data.toString();};
            //this.add = function(newKey, newValue, overwrite){
            //    var flag = true;
            //    var type = typeOf(data);
            //    if (!this.hasOwnProperty(newKey)){
            //        if (type=="array"){
            //            if (arguments.length<2){
            //                data.push(newKey);
            //                newValue = newKey;
            //                newKey = data.length-1;
            //            }else{
            //                debugger;
            //                if (!newKey && newKey!=0){
            //                    data.push(newValue);
            //                    newKey = data.length-1;
            //                }else{
            //                    flag == false;
            //                }
            //            }
            //        }else{
            //            data[newKey] = newValue;
            //        }
            //        //var valueType = typeOf(newValue);
            //        //var newValueData = newValue;
            //        //if (valueType=="object" || valueType=="array") newValueData = new MWF.xScript.JSONData(newValue, callback, newKey, this);
            //        //if (valueType=="null") newValueData = new MWF.xScript.JSONData({}, callback, newKey, this);
            //        if (flag){
            //            var o = {};
            //            o[newKey] = {"configurable": true, "enumerable": true, "get": getter.apply(this, [data, callback, newKey, this]),"set": setter.apply(this, [data, callback, newKey, this])};
            //            MWF.defineProperties(this, o);
            //        }
            //        this[newKey] = newValue;
            //    }else{
            //        if (overwrite) this[newKey] = newValue;
            //    }
            //
            //    //var valueType = typeOf(newValue);
            //    //var newValueData = newValue;
            //    //if (valueType=="object" || valueType=="array") newValueData = new MWF.xScript.JSONData(newValue, callback, newKey, this);
            //    //if (valueType=="null") newValueData = new MWF.xScript.JSONData({}, callback, newKey, this);
            //    //
            //    //this[newKey] = newValueData;
            //
            //    return this[newKey];
            //};
            //this.del = function(delKey){
            //    if (!this.hasOwnProperty(delKey)) return null;
            //    delete data[newKey];
            //    delete this[newKey];
            //    return this;
            //};
        };

        var type = typeOf(data);
        if (type === "object" || type === "array") define.apply(this);
    };
}
if( !MWF.xScript.dictLoaded )MWF.xScript.dictLoaded = {};

if (!MWF.xScript.createDict) {
    MWF.xScript.addDictToCache = function (options, path, json) {
        if (!path) path = "root";
        if (path.indexOf("root") !== 0) path = "root." + path;

        var type = options.appType || "process";
        var enableAnonymous = ( options.enableAnonymous || options.anonymous ) || false;

        var appFlagList = [];
        if (options.application) appFlagList.push(options.application);
        if (options.appId) appFlagList.push(options.appId);
        if (options.appName) appFlagList.push(options.appName);
        if (options.appAlias) appFlagList.push(options.appAlias);

        var dictFlagList = [];
        if (options.id) dictFlagList.push(options.id);
        if (options.name) dictFlagList.push(options.name);
        if (options.alias) dictFlagList.push(options.alias);

        var cache = {};
        cache[path] = json;

        for (var i = 0; i < appFlagList.length; i++) {
            for (var j = 0; j < dictFlagList.length; j++) {
                var k = dictFlagList[j] + type + appFlagList[i] + enableAnonymous;
                if (!MWF.xScript.dictLoaded[k]) {
                    MWF.xScript.dictLoaded[k] = cache; //같은 객체를 가리킴
                    // MWF.xScript.dictLoaded[k][path] = json; //서로 다른 객체를 가리킴
                } else if (i === 0 && j === 0) {
                    MWF.xScript.setDictToCache(k, path, json);
                    var arr = path.split(/\./g);
                    var p;
                    var cache = MWF.xScript.dictLoaded[k];
                    for (var l = 0; l < arr.length; l++) {
                        p = l === 0 ? arr[0] : (p + "." + arr[l]);
                        if (cache[p]) break;
                    }
                    if (p) {
                        var mathP = p + ".";
                        Object.keys(cache).each(function (path, idx) {
                            if (path.indexOf(mathP) === 0) delete cache[path];
                        })
                    }
                }
            }
        }
    };

    MWF.xScript.getMatchedDict = function (key, path) {
        if (!path) path = "root";
        if (path.indexOf("root") !== 0) path = "root." + path;

        var arr = path.split(/\./g);
        if (MWF.xScript.dictLoaded[key]) {
            var dicts = MWF.xScript.dictLoaded[key];
            var list = Array.clone(arr);
            var p;
            var dict;
            for (var i = 0; i < arr.length; i++) {
                p = i === 0 ? arr[0] : (p + "." + arr[i]);
                list.shift();
                if (dicts[p]) {
                    dict = dicts[p];
                    break;
                }
            }
            return {
                dict: dict,
                unmatchedPathList: list
            }
        }
        return {
            dict: null,
            unmatchedPathList: list
        }
    };

    MWF.xScript.insertDictToCache = function (key, path, json) {
        var p = path;
        if( !p )p = "root";
        if( p.indexOf("root") !== 0 )p = "root." + p ;

        if (MWF.xScript.dictLoaded[key]) {
            var matchedDict = MWF.xScript.getMatchedDict(key, path);
            var dict = matchedDict.dict;
            var list = matchedDict.unmatchedPathList;
            if (!dict) {
                MWF.xScript.dictLoaded[key][p] = json;
            }else if( !list || list.length === 0 ){
                MWF.xScript.dictLoaded[key][p] = json;
            } else {
                for (var j = 0; j < list.length - 1; j++) {
                    if (!dict[list[j]]) {
                        dict[list[j]] = {};
                    }
                    dict = dict[list[j]];
                }
                var lastPath = list[list.length - 1];
                if (!dict[lastPath]) {
                    dict[lastPath] = json;
                } else if (typeOf(dict[lastPath]) === "array") {
                    dict[lastPath].push(json);
                }
            }
        } else {
            MWF.xScript.dictLoaded[key] = {};
            MWF.xScript.dictLoaded[key][p] = json;
        }
    };

    MWF.xScript.setDictToCache = function (key, path, json) {
        var p = path;
        if( !p )p = "root";
        if( p.indexOf("root") !== 0 )p = "root." + p ;

        if (MWF.xScript.dictLoaded[key]) {
            var matchedDict = MWF.xScript.getMatchedDict(key, path);
            var dict = matchedDict.dict;
            var list = matchedDict.unmatchedPathList;
            if (!dict) {
                MWF.xScript.dictLoaded[key][p] = json;
            }else if( !list || list.length === 0 ){
                MWF.xScript.dictLoaded[key][p] = json;
            } else {
                for (var j = 0; j < list.length - 1; j++) {
                    if (!dict[list[j]]) {
                        dict[list[j]] = {};
                    }
                    dict = dict[list[j]];
                }
                dict[list[list.length - 1]] = json;
            }
        } else {
            MWF.xScript.dictLoaded[key] = {};
            MWF.xScript.dictLoaded[key][p] = json;
        }
    };

    MWF.xScript.getDictFromCache = function (key, path) {
        var matchedDict = MWF.xScript.getMatchedDict(key, path);
        var dict = matchedDict.dict;
        var list = matchedDict.unmatchedPathList;
        if (dict) {
            for (var j = 0; j < list.length; j++) {
                dict = dict[list[j]];
                if (!dict) return null;
            }
            return dict;
        }
        return null;
    };

    MWF.xScript.deleteDictToCache = function (key, path) {
        var matchedDict = MWF.xScript.getMatchedDict(key, path);
        var dict = matchedDict.dict;
        var list = matchedDict.unmatchedPathList;

        if (dict) {
            for (var j = 0; j < list.length - 1; j++) {
                dict = dict[list[j]];
                if (!dict) return;
            }
            if( list.length ){
                delete dict[list[list.length - 1]];
            }
        }
    };

    MWF.xScript.createDict = function (application, appType) {
        //optionsOrName : {
        //  type : "", //기본값 process, process  cms 가능
        //  application : "", //프로세스/CMS의 이름/별칭/id, 기본값은 현재 앱
        //  name : "", // 데이터 사전 이름/별칭/id
        //  anonymous : false //로그인하지 않은 상태에서 CMS 데이터 사전 읽기 허용, 이 매개변수 이름은 enableAnonymous도 가능
        //}
        //또는 name: "" // 데이터 사전 이름/별칭/id
        return function (optionsOrName) {
            var options = optionsOrName;
            if (typeOf(options) == "string") {
                options = {
                    name: options,
                    type: appType,
                    application: application
                };
            }
            var name = this.name = options.name;
            var type;
            if( options.type === "service"){
                type = options.type;
            }else{
                type = ( options.type && options.application ) ?  options.type : "process";
            }
            var applicationId = options.application || application;
            var enableAnonymous = ( options.enableAnonymous || options.anonymous ) || false;

            var opt = {
                "appType": type,
                "name": name,
                "appId": applicationId,
                "enableAnonymous": enableAnonymous
            };

            var key = name + type + applicationId + enableAnonymous;
            // if (!dictLoaded[key]) dictLoaded[key] = {};
            // this.dictData = dictLoaded[key];

            //MWF.require("MWF.xScript.Actions.DictActions", null, false);
            var action;
            if (type === "cms") {
                action = MWF.Actions.get("x_cms_assemble_control");
            } else if( type === "portal" ){
                action = MWF.Actions.get("x_portal_assemble_surface");
            }else if( type === "service" ){
                key = name+type+enableAnonymous;
                action = MWF.Actions.get("x_program_center");
            } else {
                action = MWF.Actions.get("x_processplatform_assemble_surface");
            }

            var encodePath = function (path) {
                var arr = path.split(/\./g);
                var ar = arr.map(function (v) {
                    return encodeURIComponent(v);
                });
                return ( type === "portal" || type === "service" ) ? ar.join(".") : ar.join("/");
            };

            this.get = function (path, success, failure, async, refresh) {
                var value = null;

                if (success === true) async = true;
                if (failure === true) async = true;

                if (!refresh) {
                    var data = MWF.xScript.getDictFromCache(key, path);
                    if (data) {
                        if (success && o2.typeOf(success) == "function") success(data);
                        if (!!async) {
                            return Promise.resolve(data);
                        } else {
                            return data;
                        }
                    }
                }

                // var cb = function(json){
                //     value = json.data;
                //     MWF.xScript.addDictToCache(opt, path, value);
                //     if (success && o2.typeOf(success)=="function") value = success(json.data);
                //     return value;
                // }.ag().catch(function(xhr, text, error){ if (failure && o2.typeOf(failure)=="function") return failure(xhr, text, error); });

                var cb = function (json) {
                    value = json.data;
                    MWF.xScript.addDictToCache(opt, path, value);
                    if (success && o2.typeOf(success) == "function") value = success(json.data);
                    return value;
                };

                var promise;
                if( type === "service" ){
                    if (path){
                        var p = encodePath( path );
                        promise = action.getDictData(encodeURIComponent(this.name), p, cb, null, !!async, false);
                    }else{
                        promise = action.getDictRoot(this.name, cb, null, !!async, false);
                    }
                }else{
                    if (path) {
                        var p = encodePath(path);
                        promise = action[((enableAnonymous && type == "cms") ? "getDictDataAnonymous" : "getDictData")](encodeURIComponent(this.name), applicationId, p, cb, null, !!async, false);
                    } else {
                        promise = action[((enableAnonymous && type == "cms") ? "getDictRootAnonymous" : "getDictRoot")](this.name, applicationId, cb, null, !!async, false);
                    }
                }
                return (!!async) ? promise : value;

                // if (path){
                //     var p = encodePath( path );
                //     //var p = path.replace(/\./g, "/");
                //     action[ ( (enableAnonymous && type == "cms") ? "getDictDataAnonymous" : "getDictData" ) ](encodeURIComponent(this.name), applicationId, p, function(json){
                //         value = json.data;
                //         // this.dictData[path] = value;
                //         MWF.xScript.addDictToCache(opt, path, value);
                //         if (success) success(json.data);
                //     }.bind(this), function(xhr, text, error){
                //         if (failure) failure(xhr, text, error);
                //     }, !!async);
                // }else{
                //     action[ ( (enableAnonymous && type == "cms") ? "getDictRootAnonymous" : "getDictRoot" ) ](this.name, applicationId, function(json){
                //         value = json.data;
                //         // this.dictData["root"] = value;
                //         MWF.xScript.addDictToCache(opt, path, value);
                //         if (success) success(json.data);
                //     }.bind(this), function(xhr, text, error){
                //         if (failure) failure(xhr, text, error);
                //     }, !!async);
                // }

                //return value;
            };

            this.set = function (path, value, success, failure) {
                var p = encodePath(path);
                //var p = path.replace(/\./g, "/");
                var successCallback = function(json){
                    MWF.xScript.setDictToCache(key, path, value);
                    if (success) return success(json.data);
                };
                var failureCallback = function(xhr, text, error){
                    if (failure) return failure(xhr, text, error);
                };
                if( type === "service" ){
                    return action.setDictData(encodeURIComponent(this.name), p, value, successCallback, failureCallback, false, false);
                }else{
                    return action.setDictData(encodeURIComponent(this.name), applicationId, p, value, successCallback, failureCallback, false, false);
                }
            };
            this.add = function (path, value, success, failure) {
                var p = encodePath(path);
                //var p = path.replace(/\./g, "/");
                var successCallback = function(json){
                    MWF.xScript.insertDictToCache(key, path, value);
                    if (success) return success(json.data);
                };
                var failureCallback = function(xhr, text, error){
                    if (failure) return failure(xhr, text, error);
                };
                if( type === "service" ) {
                    return action.addDictData(encodeURIComponent(this.name), p, value, successCallback, failureCallback, false, false);
                }else{
                    return action.addDictData(encodeURIComponent(this.name), applicationId, p, value, successCallback, failureCallback, false, false);
                }
            };
            this["delete"] = function (path, success, failure) {
                var p = encodePath(path);
                //var p = path.replace(/\./g, "/");
                var successCallback = function(json){
                    MWF.xScript.deleteDictToCache(key, path);
                    if (success) return success(json.data);
                };
                var failureCallback = function(xhr, text, error){
                    if (failure) return failure(xhr, text, error);
                };
                if( type === "service" ) {
                    return action.deleteDictData(encodeURIComponent(this.name), p, successCallback, failureCallback, false, false);
                }else{
                    return action.deleteDictData(encodeURIComponent(this.name), applicationId, p, successCallback, failureCallback, false, false);
                }
            };
            this.destory = this["delete"];
        }
    };
}
