MWF.xAction = MWF.xAction || {};
//MWF.require("MWF.xDesktop.Actions.RestActions", null, false);

/**
 * CryptoJS로 동기 AES-GCM 복호화
 * @param {string} base64Payload - Java 측에서 보낸 [12바이트 IV] + [암호문+Tag] Base64 문자열
 * @param {string} keyString - 키 문자열 (예: 16바이트)
 * @returns {string} 복호화된 UTF-8 문자열
 */
var keyString = '98b25ee736a4db745e9b66fe46274fd8';
function decryptAesGcmSync(base64Payload, keyString) {
  try {
    // 1. Base64 문자열을 SJCL의 bitArray로 파싱
    const payloadBitArray = sjcl.codec.base64.toBits(base64Payload);

    // 2. 앞 12바이트(96비트)를 IV로 추출
    const ivBitArray = sjcl.bitArray.bitSlice(payloadBitArray, 0, 96);

    // 3. 12바이트 이후의 모든 데이터 추출(암호문 + 16바이트 Tag)
    const ciphertextBitArray = sjcl.bitArray.bitSlice(payloadBitArray, 96);

    // 4. 키 문자열을 bitArray로 변환
    const keyBitArray = sjcl.codec.utf8String.toBits(keyString);

    // 5. SJCL AES-GCM 복호화기 생성
    const cipher = new sjcl.cipher.aes(keyBitArray);

    // 6.【동기 복호화】GCM 모드 복호화 실행 (기본 128비트 Tag 검증)
    const decryptedBitArray = sjcl.mode.gcm.decrypt(
      cipher,
      ciphertextBitArray,
      ivBitArray,
      [], // adata (추가 인증 데이터, 없으면 빈 배열 전달)
      128 // tlen (Tag 길이, Java의 128비트에 대응)
    );

    // 7. 복호화한 bitArray를 UTF-8 텍스트로 복원
    return sjcl.codec.utf8String.fromBits(decryptedBitArray);
  } catch (error) {
    throw new Error("동기 복호화에 실패했습니다. 암호문이 손상되었거나 키가 일치하지 않습니다. 오류 상세: " + error.message);
  }
}

MWF.xAction.RestActions = MWF.Actions = {
    "actions": {},
    "loadedActions": {},
    "get": function(root){
        if (this.actions[root]) return this.actions[root];

        var actions = null;
        var url = o2.session.path+"/xAction/services/"+root+".json";
        MWF.getJSON(url, function(json){actions = json;}.bind(this), false, false, false);

        if (!MWF.xAction.RestActions.Action[root] && actions.clazz) MWF.require("MWF.xAction.services."+actions.clazz, null, false);
        if (!MWF.xAction.RestActions.Action[root]) MWF.xAction.RestActions.Action[root] = new Class({Extends: MWF.xAction.RestActions.Action});

        this.actions[root] = new MWF.xAction.RestActions.Action[root](root, actions);
        return this.actions[root];
    },
    "load": function(root){
        if (this.loadedActions[root]) return this.loadedActions[root];
        var jaxrs = null;
        //var url = this.getHost(root)+"/"+root+"/describe/describe.json";
        // var url = this.getHost(root)+"/"+root+"/describe/api.json";

        var url = this.getHost(root)+"/"+root+"/jaxrs/describe";
  
        try{
            MWF.getJSON(url, function(json){
                var dataText = json.data.data;
                // dataText 복호화
                dataText = decryptAesGcmSync(dataText, keyString);
                jaxrs = JSON.parse(dataText).jaxrs;
            }.bind(this), false, false, false);
            if (jaxrs){
                var actionObj = {};
                jaxrs.each(function(o){
                    if (o.methods && o.methods.length){
                        var actions = {};
                        o.methods.each(function(m){
                            var o = {"uri": "/"+m.uri};
                            if (m.method) o.method = m.method;
                            if (m.enctype) o.enctype = m.enctype;
                            actions[m.name] = o;
                        }.bind(this));
                        actionObj[o.name] = new MWF.xAction.RestActions.Action(root, actions);
                        //actionObj[o.name] = new MWF.xAction.RestActions.Action(root, o.methods);
                    }
                }.bind(this));
                this.loadedActions[root] = actionObj;
                return actionObj;
            }
        }catch(e){}
        return null;
    },
    //actions: [{"action": "", "subAction": "TaskAction", "name": "list", "par": [], "body": "",  "urlEncode"： false, "cache": false}]
    async: function(actions, callback){
        var cbs = (o2.typeOf(callback)==="function") ? callback : callback.success;
        var cbf = (o2.typeOf(callback)==="function") ? null : callback.failure;
        var res = [];
        var len = actions.length;
        var jsons = new Array(len-1);

        var cb = function(){
            if (res.length===len) cbs.apply(this, jsons);
        };
        var _doError = function(xhr, text, error){
            if (xhr.status!=0){
                var errorText = error;
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    if (json){
                        errorText = json.message.trim() || "request json error";
                    }else{
                        errorText = "request json error: "+xhr.responseText;
                    }
                }
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
            }
        };

        actions.each(function(action, i){
            var actionArgs = action.par || [];
            actionArgs.push(function(json){
                jsons[i] = json;
                res.push(true);
                cb();
            });
            actionArgs.push(function(xhr, text, error){
                res.push(false);
                if (!cbf){
                    _doError(xhr, text, error);
                }else{
                    cbf();
                }
                cb();
            });
            actionArgs.push(true);
            actionArgs.push(action.urlEncode);
            actionArgs.push(action.cache);
            action.action[action.subAction][action.name].apply(action.action[action.subAction], actionArgs);
        });
    },

    //actions: [{"action": "", "name": "list", "par": [], "body": "",  "urlEncode"： false, "cache": false}]
    invokeAsync2: function(actions, callback){

        var cbs = (o2.typeOf(callback)==="function") ? callback : callback.success;
        var cbf = (o2.typeOf(callback)==="function") ? null : callback.failure;
        var res = [];
        var len = actions.length;
        var jsons = new Array(len-1);

        var cb = function(){
            if (res.length===len) cbs.apply(this, jsons);
        };
        var _doError = function(xhr, text, error){
            if (xhr.status!=0){
                var errorText = error;
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    if (json){
                        errorText = json.message.trim() || "request json error";
                    }else{
                        errorText = "request json error: "+xhr.responseText;
                    }
                }
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
            }
        };

        actions.each(function(action, i){
            var actionArgs = action.par || [];
            actionArgs.push(function(json){
                jsons[i] = json;
                res.push(true);
                cb();
            });
            actionArgs.push(function(xhr, text, error){
                res.push(false);
                if (!cbf){
                    _doError(xhr, text, error);
                }else{
                    cbf();
                }
                cb();
            });
            actionArgs.push(true);
            actionArgs.push(action.urlEncode);
            actionArgs.push(action.cache);
            action.action[action.name].apply(action.action, actionArgs);
        });
    },

    "getHost": function(root){
        var addressObj = layout.serviceAddressList[root];
        var address = "";

        var defaultPort = layout.config.app_protocol==='https' ? "443" : "80";
        if (addressObj){
            var appPort = addressObj.port || window.location.port;
            address = layout.config.app_protocol+"//"+(addressObj.host || window.location.hostname)+ ((!appPort || appPort.toString()===defaultPort) ? "" : ":"+appPort);
        }else{
            var host = layout.desktop.centerServer.host || window.location.hostname;
            var port = layout.desktop.centerServer.port || window.location.port;
            //var mapping = layout.getCenterUrlMapping();
            address = layout.config.app_protocol+"//"+host+( (!port || port.toString()===defaultPort) ? "" : ":"+port);
        }
        return address;
    },
    "invokeAsync": function(actions, callback){
        var len = actions.length;
        var parlen = arguments.length-2;
        var res = [];
        var jsons = new Array(len-1);
        var args = arguments;

        var cbs = (o2.typeOf(callback)==="function") ? callback : callback.success;
        var cbf = (o2.typeOf(callback)==="function") ? null : callback.failure;

        var cb = function(){
            if (res.length===len) cbs.apply(this, jsons);
        };
        var _doError = function(xhr, text, error){
            if (xhr.status!=0){
                var errorText = error;
                if (xhr){
                    var json = JSON.decode(xhr.responseText);
                    if (json){
                        errorText = json.message.trim() || "request json error";
                    }else{
                        errorText = "request json error: "+xhr.responseText;
                    }
                }
                MWF.xDesktop.notice("error", {x: "right", y:"top"}, errorText);
            }
        };

        actions.each(function(action, i){
            var par = (i<parlen) ? args[i+2] : args[parlen+1];
            if (par){
                var actionArgs = (o2.typeOf(par)==="array") ? par : [par];
                actionArgs.unshift(function(xhr, text, error){
                    res.push(false);
                    if (!cbf){
                        _doError(xhr, text, error);
                    }else{
                        cbf();
                    }
                    cb();
                    return true;
                });
                //actionArgs.unshift(null);
                actionArgs.unshift(function(json){
                    jsons[i] = json;
                    res.push(true);
                    cb();
                });

                var p = action.action[action.name].apply(action.action, actionArgs);
                // p.catch(function(xhr, text, error){
                //     res.push(false);
                //     if (!cbf){
                //         _doError(xhr, text, error);
                //     }else{
                //         cbf();
                //     }
                //     cb();
                // })

            }else{
                action.action[action.name](function(){
                    jsons[i] = json;
                    res.push(true);
                    cb();
                }, function(xhr, text, error){
                    res.push(false);
                    if (!cbf){
                        _doError(xhr, text, error);
                    }else{
                        cbf();
                    }
                    cb();
                });
            }
        });
    }
};
MWF.xAction.RestActions.Action = new Class({
    initialize: function(root, actions){
        this.action = new MWF.xDesktop.Actions.RestActions("/xAction/services/"+root+".json", root, "");
        this.action.actions = actions;

        Object.each(this.action.actions, function(service, key){
            if (service.uri) if (!this[key]) this.createMethod(service, key);
        }.bind(this));
    },
    createMethod: function(service, key){
        var jaxrsUri = service.uri;
        var re = new RegExp("\{.+?\}", "g");
        var replaceWords = jaxrsUri.match(re);
        var parameters = [];
        if (replaceWords) parameters = replaceWords.map(function(s){
            return s.substring(1,s.length-1);
        });

        this[key] = this.invokeFunction(service, parameters, key);
    },
    invokeFunction: function(service, parameters, key){
        //uri의 매개변수, data(post, put), file(formData), success, failure, async
        return function(){
            var i = parameters.length-1;
            var n = arguments.length;
            var functionArguments = arguments;
            var parameter = {};
            var success, failure, async, data, file;
            if (typeOf(functionArguments[0])==="function"){
                i=-1;
                success = (n>++i) ? functionArguments[i] : null;
                failure = (n>++i) ? functionArguments[i] : null;
                parameters.each(function(p, x){
                    parameter[p] = (n>++i) ? functionArguments[i] : null;
                });
                if (service.method && (service.method.toLowerCase()==="post" || service.method.toLowerCase()==="put")){
                    if ((!service.enctype) || service.enctype.toLowerCase()!=="formdata"){
                        data = (n>++i) ? functionArguments[i] : null;
                    }else{
                        data = (n>++i) ? functionArguments[i] : null;
                        file = (n>++i) ? functionArguments[i] : null;
                    }
                }
                async = (n>++i) ? functionArguments[i] : null;
                urlEncode = (n>++i) ? functionArguments[i] : true;
                cache = (n>++i) ? functionArguments[i] : (Browser.name != "ie");
            }else{
                parameters.each(function(p, x){
                    parameter[p] = (n>x) ? functionArguments[x] : null;
                });
                if (service.method && (service.method.toLowerCase()==="post" || service.method.toLowerCase()==="put")){
                    if ((!service.enctype) || service.enctype.toLowerCase()!=="formdata"){
                        data = (n>++i) ? functionArguments[i] : null;
                    }else{
                        data = (n>++i) ? functionArguments[i] : null;
                        file = (n>++i) ? functionArguments[i] : null;
                    }
                }
                success = (n>++i) ? functionArguments[i] : null;
                failure = (n>++i) ? functionArguments[i] : null;
                async = (n>++i) ? functionArguments[i] : null;
                urlEncode = (n>++i) ? functionArguments[i] : true;
                cache = (n>++i) ? functionArguments[i] : (Browser.name != "ie");
            }
            return this.invoke(service,{"name": key, "async": async, "data": data, "file": file, "parameter": parameter, "success": success, "failure": failure, "urlEncode": urlEncode, "cache": cache});
            //if (!cache) debugger;
            //return this.action.invoke({"name": key, "async": async, "data": data, "file": file, "parameter": parameter, "success": success, "failure": failure, "urlEncode": urlEncode, "cache": cache});
        }.bind(this);
    },
    invoke: function(service, options){
        return this.action.invoke(options);
    }
});

