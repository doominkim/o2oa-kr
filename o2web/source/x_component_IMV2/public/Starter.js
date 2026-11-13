MWF.require("MWF.widget.UUID", null, false);
MWF.xDesktop.requireApp("Template", "MForm", null, false);
MWF.xDesktop.requireApp("Template", "MPopupForm", null, false);
MWF.xApplication.IMV2 = MWF.xApplication.IMV2 || {};
MWF.require("MWF.widget.Mask", null, false);
MWF.xDesktop.requireApp("IMV2", "lp." + o2.language, null, false);

MWF.xApplication.IMV2.Starter = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "businessType": "none", // 업무 유형: none, process, cms. 채팅 시작 시 업무 유형 포함 여부
        "businessId": null, // 업무 ID, 신규 생성 시 비어 있음
        "includePersonList": [], // 지정 범위 내 사용자 중 선택, 비어 있으면 제한 없음
        "conversationId": null, // 전달된 대화 id, 신규 생성 시 비어 있음
        "mode": "default" // 표시 모드: default onlyChat. onlyChat 모드는 conversationId를 전달해야 하며 해당 대화의 채팅 창을 열고 왼쪽 대화 목록을 숨김
    },
    initialize: function (data, app, options) {
        console.log("init   IMV2.Starter ");
        this.setOptions(options);
        this.path = "../x_component_IMV2/$Main/";
        MWF.xDesktop.requireApp("IMV2", "lp." + o2.language, null, false);
        this.lp = MWF.xApplication.IMV2.LP;
        this.data = data;
        this.app = app;
    },
    load: function () {
        console.log("init   IMV2.load    ");
        debugger;
        // 기본 채팅 생성
        if (!(this.options.businessType) || this.options.businessType === "none" || !(this.options.businessId) || this.options.businessId === "") {
            var form = new MWF.xApplication.IMV2.Starter.CreateConversationForm(this);
            form.create()
        } else if (this.options.businessType === "process") {
            if (this.options.businessId) {
                //TODO 이미 존재할 때 여러 개 생성을 허용할지 고려, 그렇다면 새로 만들지 기존 것을 열지 안내 필요
                console.log("결재 job id로 대화방이 있는지 조회합니다. 있으면 열고, 없으면 만듭니다. jobId: " + this.options.businessId);
                this.findConversationByBusinessId();
            } else {
                this.app.notice(this.lp.msgNoBusinessId, "error");
            }
            //업무 문서 열기 o2.env.form.openJob
        } else {
            this.app.notice(this.lp.msgNotSupport, "error");
        }

    },
    showLoading: function () {
        const style = document.createElement('style');
        style.id = 'loading-style';
        style.textContent = `
        .loading-overlay {
          position: fixed;
          top: 0;
          left: 0; 
          width: 100vw;
          height: 100vh;
          background: rgba(0, 0, 0, 0.5);
          display: flex;
          align-items: center;
          justify-content: center;
          z-index: 1000;
          color: #fff;
          gap: 10px;
        }
        .loading-spinner {
          border: 5px solid rgba(255, 255, 255, 0.5);
          border-top: 5px solid #fff;
          border-radius: 50%;
          width: 50px;
          height: 50px;
          animation: spin 1s linear infinite;
        }
        @keyframes spin {
          0% {
            transform: rotate(0deg);
          }
          100% {
            transform: rotate(360deg);
          }
        }
      `;
        document.head.appendChild(style);
        const overlay = document.createElement('div');
        overlay.className = 'loading-overlay';
        const spinner = document.createElement('div');
        spinner.className = 'loading-spinner';
        const text = document.createTextNode('불러오는 중...');
        overlay.appendChild(spinner);
        overlay.appendChild(text);
        document.body.appendChild(overlay);
    },
    hideLoading: function () {
        const overlay = document.querySelector('.loading-overlay');
        if (overlay) {
            document.body.removeChild(overlay);
        }
        const loadingStyle = document.getElementById('loading-style');
        if (loadingStyle) {
            document.head.removeChild(loadingStyle);
        }
    },
    // 대화 채팅 창 열기
    openConversationWindow: function (conversation) {
        var options = {
            conversationId: conversation.id,
            openConversation: conversation,
            mode: this.options.mode || "default"
        }
        layout.openApplication(null, "IMV2", options);
    },
    // 대화 생성
    newConversation: function (personList) {
        console.log("newConversation", personList);
        this.showLoading()
        var conv = {
            type: "group",
            personList: personList,
        };
        if (this.options.businessId) {
            conv.businessId = this.options.businessId;
            conv.businessType = this.options.businessType;
        }
        o2.Actions.load("x_message_assemble_communicate").ImAction.create(conv, function (json) {
            var newConv = json.data;
            if (this.app.refreshAll) this.app.refreshAll();
            this.hideLoading()
            this.openConversationWindow(newConv);
        }.bind(this), function (error) {
            console.log(error);
            if (this.app.refreshAll) this.app.refreshAll();
            this.hideLoading()
        }.bind(this))
    },
    // 프로세스의 job id로 대화 존재 여부 조회
    findConversationByBusinessId: function () {
        o2.Actions.load("x_message_assemble_communicate").ImAction.conversationFindByBusinessId(this.options.businessId, function (json) {
            if (json.data && json.data.length > 0) {
                this.showChooseConversationDialog(json.data);
            } else {
                if (this.options.businessType === "process") {
                    this.getProcessReviewByJobId();
                }
            }
        }.bind(this), function (error) {
            console.log(error);
            if (this.options.businessType === "process") {
                this.getProcessReviewByJobId();
            }
        }.bind(this));
    },
    // 기존 대화가 있으면 클릭해 대화 채팅을 열 수 있도록 표시
    showChooseConversationDialog: function (conversationList) {
        if (conversationList.length === 1) {
            this.openConversationWindow(conversationList[0]);
        } else if (conversationList.length > 1) {
            var cssPath = this.path + "default/style.css";
            var chooseConversationHtmlPath = this.path + "default/chooseConversation.html";
            this.app.content.loadAll({
                "css": [cssPath],
                "html": chooseConversationHtmlPath,
            }, {"bind": {"lp": this.lp, "data": {}}, "module": this}, function () {
                // 대화 목록
                for (let index = 0; index < conversationList.length; index++) {
                    const conv = conversationList[index];
                    var conversationItem = new Element("div", {"class": "conversation-item"}).inject(this.conversationChooseListNode);
                    new Element("span", {"text": "【" + conv.title + "】"}).inject(conversationItem);
                    new Element("span", {"text": "열기", "style": "color: #4A90E2;"}).inject(conversationItem);
                    conversationItem.store("conversation", conv);
                    conversationItem.addEvents({
                        "click": function (e) {
                            debugger;
                            var myConv = null;
                            if (e.target.get("tag") === "span") {
                                myConv = e.target.parentNode.retrieve("conversation");
                            } else {
                                myConv = e.target.retrieve("conversation");
                            }
                            if (myConv) {
                                this.openConversationWindow(myConv);
                                this.closeChooseConversationDialog();
                            }

                        }.bind(this)
                    });
                }
                // 닫기 버튼
                this.conversationChooseCloseNode.addEvents({
                    "click": function (e) {
                        this.closeChooseConversationDialog();
                    }.bind(this)
                });
                // 새 채팅 버튼
                this.conversationCreateNewNode.addEvents({
                    "click": function (e) {
                        this.closeChooseConversationDialog();
                        if (this.options.businessType === "process") {
                            this.getProcessReviewByJobId();
                        }
                    }.bind(this)
                })
            }.bind(this));
        }
    },
    // 대화 선택 창 닫기
    closeChooseConversationDialog: function () {
        if (this.conversationChooseDialogNode) {
            this.conversationChooseDialogNode.destroy();
            this.conversationChooseDialogNode = null;
        }
        if (this.app.refreshAll) this.app.refreshAll();
    },
    // 대화 생성 양식 표시, 사용자 목록을 나열해 사용자 선택 가능
    showCreateConversationWithPersonsDialog: function (personList) {
        if (personList && personList.length > 0) {
            var cssPath = this.path + "default/style.css";
            var choosePersonHtmlPath = this.path + "default/choosePerson.html";
            this.app.content.loadAll({
                "css": [cssPath],
                "html": choosePersonHtmlPath,
            }, {"bind": {"lp": this.lp, "data": {}}, "module": this}, function () {
                //로드 완료 후 콜백
                // 사용자 목록
                for (let index = 0; index < personList.length; index++) {
                    const person = personList[index];
                    // <div class="person-tag person-tag-active">홍길동</div>
                    var personTag = new Element("div", {"class": "person-tag person-tag-active"}).inject(this.personListNode);
                    var name = person;
                    var cutIndex = person.indexOf("@")
                    if (cutIndex > -1) {
                        name = name.substring(0, cutIndex);
                    }
                    personTag.set("text", name);
                    personTag.store("person", person);
                    personTag.addEvents({
                        "click": function (e) {
                            if (e.target.get("class") === "person-tag person-tag-active") {
                                e.target.set("class", "person-tag");
                            } else {
                                e.target.set("class", "person-tag person-tag-active");
                            }
                        }
                    });
                }
                // 대화 생성 버튼
                this.personChooseCreateNode.addEvents({
                    "click": function (e) {
                        var personList = []
                        this.personListNode.getChildren().each(function (tag) {
                            if (tag.get("class") === "person-tag person-tag-active") {
                                personList.push(tag.retrieve("person"));
                            }
                        });
                        if (personList.length > 0) {
                            this.newConversation(personList);
                            this.closeChoosePersonDialog();
                        } else {
                            this.app.notice(this.lp.msgNeedChoosePerson, "error");
                        }
                    }.bind(this)
                });
                // 닫기 버튼
                this.personChooseCloseBtnNode.addEvents({
                    "click": function (e) {
                        this.closeChoosePersonDialog();
                    }.bind(this)
                })
            }.bind(this));

        } else {
            this.app.notice(this.lp.msgNoBusinessPerson, "error");
        }
    },
    // 사용자 선택 닫기
    closeChoosePersonDialog: function () {
        if (this.personChooseDialogNode) {
            this.personChooseDialogNode.destroy();
            this.personChooseDialogNode = null;
        }
        if (this.app.refreshAll) this.app.refreshAll();
    },

    // jobid로 review를 조회해 관련된 모든 사용자 가져오기
    getProcessReviewByJobId: function () {
        o2.Actions.load("x_processplatform_assemble_surface").ReviewAction.listWithJob(this.options.businessId, function (json) {
            if (json.data && json.data.length > 0) {
                var personList = [];
                json.data.each(function (review) {
                    if (review.person && review.person !== layout.session.user.distinguishedName) {
                        personList.push(review.person);
                    }
                });
                // this.showCreateConversationWithPersonsDialog(personList);
                // 바로 대화 생성
                this.newConversation(personList);
            } else {
                // this.showCreateConversationWithPersonsDialog([]);
                this.app.notice(this.lp.msgNoBusinessPerson, "error");
            }
        }.bind(this), function (error) {
            console.log(error);
            // this.showCreateConversationWithPersonsDialog([]);
            this.app.notice(this.lp.msgNoBusinessPerson, "error");
        }.bind(this));
    }


});


//채팅 생성 팝업 양식
MWF.xApplication.IMV2.Starter.CreateConversationForm = new Class({
    Extends: MPopupForm,
    Implements: [Options, Events],
    options: {
        "style": "minder",
        "width": 700,
        "height": "200",
        "hasTop": true,
        "hasIcon": false,
        "draggable": true,
        "title": MWF.xApplication.IMV2.LP.createConversation,
        "includePersonList": [], // 지정 범위 내 사용자 중 선택, 비어 있으면 제한 없음
        "personSelected": []
    },
    _createTableContent: function () {
        var html = "<table width='100%' bordr='0' cellpadding='7' cellspacing='0' styles='formTable' style='margin-top: 20px; '>" +
            "<tr><td styles='formTableTitle' lable='person' width='25%'></td>" +
            "    <td styles='formTableValue14' item='person' colspan='3'></td></tr>" +
            "</table>";
        this.formTableArea.set("html", html);
        var me = layout.session.user.distinguishedName;
        var exclude = [];
        if (me) {
            exclude = [me];
        }
        this.form = new MForm(this.formTableArea, this.data || {}, {
            isEdited: true,
            style: "minder",
            hasColon: true,
            itemTemplate: {
                person: {
                    text: MWF.xApplication.IMV2.LP.selectPerson,
                    type: "org",
                    orgType: "person",
                    notEmpty: true,
                    exclude: exclude,
                    count: 0, // 선택 가능 인원 수, 0이면 인원 제한 없음
                    value: this.options["personSelected"]
                },
            }
        }, this.app);
        this.form.load();

    },
    _createBottomContent: function () {
        if (this.isNew || this.isEdited) {
            this.okActionNode = new Element("button.inputOkButton", {
                "styles": this.css.inputOkButton,
                "text": MWF.xApplication.IMV2.LP.ok
            }).inject(this.formBottomNode);
            this.okActionNode.addEvent("click", function (e) {
                this.save(e);
            }.bind(this));
        }
        this.cancelActionNode = new Element("button.inputCancelButton", {
            "styles": (this.isEdited || this.isNew || this.getEditPermission()) ? this.css.inputCancelButton : this.css.inputCancelButton_long,
            "text": MWF.xApplication.IMV2.LP.close
        }).inject(this.formBottomNode);
        this.cancelActionNode.addEvent("click", function (e) {
            this.close(e);
        }.bind(this));

    },
    save: function () {
        var data = this.form.getResult(true, null, true, false, true);
        if (data) {
            this.explorer.newConversation(data.person);
            this.close();
        }
    }
});


/**
 * 메시지 공유, 메시지 전달
 */
MWF.xApplication.IMV2.ShareToConversation = new Class({
    Extends: MWF.widget.Common,
    Implements: [Options, Events],
    options: {
        "style": "default",
        "businessType": "none", // 업무 유형: none, process, cms. 채팅 시작 시 업무 유형 포함 여부
        "businessId": null, // 업무 ID, 신규 생성 시 비어 있음
        "includePersonList": [], // 지정 범위 내 사용자 중 선택, 비어 있으면 제한 없음
        "conversationId": null, // 전달된 대화 id, 신규 생성 시 비어 있음
        "mode": "default" // 표시 모드: default onlyChat. onlyChat 모드는 conversationId를 전달해야 하며 해당 대화의 채팅 창을 열고 왼쪽 대화 목록을 숨김
    },
    initialize: function (data, app, options) {
        console.log("init   IMV2.Starter ");
        this.setOptions(options);
        this.path = "../x_component_IMV2/$Main/";
        MWF.xDesktop.requireApp("IMV2", "lp." + o2.language, null, false);
        this.lp = MWF.xApplication.IMV2.LP;
        this.data = data; // msgBody 메시지 본문
        this.app = app;

    },
    load: function () {
        console.log("init   IMV2.ShareToConversation    ");
        debugger;
        if (this.data && this.data.msgBody) {
            console.log(this.data.msgBody);
            this.openConversationListDialog();
        } else {
            this.app.notice(this.lp.msgShareNoBody, "error");
        }
    },
    // 창 열기
    openConversationListDialog: function () {
        var cssPath = this.path + "default/style.css";
        var choosePersonHtmlPath = this.path + "default/shareToConversation.html";
        this.app.content.loadAll({
            "css": [cssPath],
            "html": choosePersonHtmlPath,
        }, {"bind": {"lp": this.lp, "data": {}}, "module": this}, function () {
            this.conversationChooseCloseBtnNode.addEvents({
                "click": function () {
                    this.closeConversationListDialog()
                }.bind(this)
            });
            this.shareSearchInputNode.addEvent("keyup", function (e) {
                if (e.code === 13) {
                    e.stopPropagation();
                    var searchInputVal = this.shareSearchInputNode.get("value");
                    console.log('검색어: ' + searchInputVal)
                    this.searchConversationAndPerson(searchInputVal);
                }
            }.bind(this));
            this.loadConversationList();
        }.bind(this));
    },
    // 창 닫기
    closeConversationListDialog: function () {
        if (this.conversationChooseDialogNode) {
            this.conversationChooseDialogNode.destroy();
            this.conversationChooseDialogNode = null;
        }
        if (this.app.refreshAll) this.app.refreshAll();
    },
    // 대화 목록 로드
    loadConversationList: function () {
        o2.Actions.load("x_message_assemble_communicate").ImAction.myConversationList(function (json) {
            if (json.data && json.data instanceof Array) {
                this.conversationList = json.data; // 대화 데이터
                this.conversationNodeItemList = [];
                for (var i = 0; i < this.conversationList.length; i++) {
                    var conversation = this.conversationList[i];
                    var itemNode = this._createConvItemNode(conversation);
                    this.conversationNodeItemList.push(itemNode);
                }
            }
        }.bind(this));
    },
    // 대화 검색
    searchConversationAndPerson: function (value) {
        this.conversationListNode.empty();
        this.searchPersonListNode.empty();
        this.searchPersonTitleNode.classList.remove("block");
        this.searchPersonTitleNode.classList.add("none");
        this.conversationNodeItemList = [];
        this.personNodeItemList = [];
        var searchConvList = [];
        if (value) {
            // 대화 검색
            searchConvList = this.conversationList.filter((c) => c.title.indexOf(value) > -1);
            //사용자 검색
            this.searchLoadPerson(value);
        } else {
            // 대화 목록 복원
            searchConvList = this.conversationList;
        }
        // 대화 목록 생성
        for (var i = 0; i < searchConvList.length; i++) {
            var conversation = searchConvList[i];
            var itemNode = this._createConvItemNode(conversation);
            this.conversationNodeItemList.push(itemNode);
        }

    },
    searchLoadPerson: function (value) {
        var body = {"key": value};
        this.searchPersonList = [];
        o2.Actions.load("x_organization_assemble_control").PersonAction.listFilterPaging(1, 20, body, function (json) {
            if (json.data && json.data instanceof Array) {
                this.searchPersonList = json.data || [];
                this.personNodeItemList = [];
                this.searchPersonTitleNode.classList.remove("none");
                this.searchPersonTitleNode.classList.add("block");
                for (var i = 0; i < this.searchPersonList.length; i++) {
                    var person = this.searchPersonList[i];
                    var personItemNode = this._createPersonItemNode(person);
                    this.personNodeItemList.push(personItemNode);
                }
            }
        }.bind(this));
    },
    _createPersonItemNode: function (person) {
        var avatarDefault = this._getIcon(person.distinguishedName);
        var itemNode = new Element("div", {"class": "item"}).inject(this.searchPersonListNode);
        var nodeBaseItem = new Element("div", {"class": "base"}).inject(itemNode);
        var avatarNode = new Element("div", {"class": "avatar"}).inject(nodeBaseItem);
        new Element("img", {"src": avatarDefault, "class": "img"}).inject(avatarNode);
        new Element("div", {"class": "body", "text": person.name}).inject(nodeBaseItem);
        itemNode.store("person", person);
        itemNode.addEvents({
            "click": function () {
                this.clickPersonItem(person)
            }.bind(this)
        })
        return itemNode;
    },
    clickPersonItem: function (person) {
        console.log('사용자 클릭', person);
        MWF.require("MWF.widget.Mask", function () {
            this.mask = new MWF.widget.Mask({"style": "desktop", "zIndex": 50000});
            this.mask.loadNode(this.app.content);
            var conv = {
                type: "single",
                personList: [person.distinguishedName],
            };
            o2.Actions.load("x_message_assemble_communicate").ImAction.create(conv, function (json) {
                var newConv = json.data;
                this.clickConversationItem(newConv);
                if (this.mask) {
                    this.mask.hide();
                    this.mask = null;
                }
            }.bind(this), function (error) {
                console.error(error);
                if (this.mask) {
                    this.mask.hide();
                    this.mask = null;
                }
            }.bind(this));
        }.bind(this));

    },
    //사용자 아바타
    _getIcon: function (id) {
        var orgAction = MWF.Actions.get("x_organization_assemble_control")
        var url = (id) ? orgAction.getPersonIcon(id) : "../x_component_IMV2/$Main/default/group.png";
        return url + "?" + (new Date().getTime());
    },
    // 대화 Node 생성
    _createConvItemNode: function (conversation) {
        var avatarDefault = this._getIcon();
        var convData = {
            "id": conversation.id,
            "avatarUrl": avatarDefault,
            "title": conversation.title,
        };
        var distinguishedName = layout.session.user.distinguishedName;
        if (conversation.type && conversation.type === "single") {
            var chatPerson = "";
            if (conversation.personList && conversation.personList instanceof Array) {
                for (var j = 0; j < conversation.personList.length; j++) {
                    var person = conversation.personList[j];
                    if (person !== distinguishedName) {
                        chatPerson = person;
                    }
                }
            }
            convData.avatarUrl = this._getIcon(chatPerson);
            var name = chatPerson;
            if (chatPerson.indexOf("@") != -1) {
                name = name.substring(0, chatPerson.indexOf("@"));
            }
            convData.title = name;
        }
        var itemNode = new Element("div", {"class": "item"}).inject(this.conversationListNode);
        var nodeBaseItem = new Element("div", {"class": "base"}).inject(itemNode);
        var avatarNode = new Element("div", {"class": "avatar"}).inject(nodeBaseItem);
        new Element("img", {"src": convData.avatarUrl, "class": "img"}).inject(avatarNode);
        new Element("div", {"class": "body", "text": convData.title}).inject(nodeBaseItem);
        itemNode.store("conversation", conversation);
        itemNode.addEvents({
            "click": function () {
                this.clickConversationItem(conversation)
            }.bind(this)
        })
        return itemNode;
    },
    // 대화 클릭
    clickConversationItem: function (conversation) {
        console.log(conversation);
        if (this.data.callback) { // 선택기
            this.data.callback(conversation)
        } else {
            var distinguishedName = layout.session.user.distinguishedName;
            var time = this._currentTime();
            var bodyJson = JSON.stringify(this.data.msgBody);
            var uuid = new MWF.widget.UUID().createTrueUUID();
            var textMessage = {
                "id": uuid,
                "conversationId": conversation.id,
                "body": bodyJson,
                "createPerson": distinguishedName,
                "createTime": time,
                "sendStatus": 1
            };
            o2.Actions.load("x_message_assemble_communicate").ImAction.msgCreate(textMessage,
                function (json) {
                    var options = {
                        conversationId: conversation.id,
                        openConversation: conversation,
                        mode: this.options.mode || "default"
                    }
                    layout.openApplication(null, "IMV2", options);
                }.bind(this),
                function (error) {
                    console.log(error);
                    this.app.notice(this.lp.msgShareError, "error");
                }.bind(this));
        }

        this.closeConversationListDialog();
    },
    _currentTime: function () {
        var today = new Date();
        var year = today.getFullYear(); //연도 가져오기
        var month = today.getMonth();//월 가져오기
        var date = today.getDate();//날짜 가져오기
        var hour = today.getHours();//시 가져오기
        var minu = today.getMinutes();//분 가져오기
        var sec = today.getSeconds();//초 가져오기
        month = month + 1;
        if (month < 10) month = "0" + month;
        if (date < 10) date = "0" + date;
        if (hour < 10) hour = "0" + hour;
        if (minu < 10) minu = "0" + minu;
        if (sec < 10) sec = "0" + sec;
        return year + "-" + month + "-" + date + " " + hour + ":" + minu + ":" + sec;
    }
});