MWF.xApplication.Selector = MWF.xApplication.Selector || {};
MWF.xDesktop.requireApp("Selector", "Person", null, false);
if(!MWF.O2Selector)MWF.O2Selector = {};
MWF.xApplication.Selector.PersonWithController = new Class({
    Extends: MWF.xApplication.Selector.Person,
    _init : function(){
        this.selectType = "person";
        this.className = "PersonWithController";
    },
    loadSelectItems: function(addToNext, lastExcludeCount ){
        //lastExcludeCount 파라미터: 이번 로드가 지난 load 때 제외된 수량을 보충하기 위한 것임을 의미
        this.page = !this.page ? 1 : this.page + 1;
        if (!this.isItemLoaded){
            if (!this.loaddingItems){
                this.loaddingItems = true;
                var count = 30;
                this._listItemNext(this.page, count, function(json){
                    if (json.data.length){
                        var excludedCount = 0;
                        json.data.each(function(data, i){
                            if( this.isExcluded( data ) ){
                                excludedCount++;
                            }else{
                                var item = this._newItem(data, this, this.itemAreaNode);
                                this.items.push(item);
                            }
                        }.bind(this));
                        this.loaddingItems = false;

                        if( lastExcludeCount ){ //지난 load 때 제외되어 로드하는 경우
                            if( count - lastExcludeCount - excludedCount < 0 ){ //이번 load 수량이 제외된 수량을 보충하기에 부족하면 다시 load 필요
                                excludedCount = lastExcludeCount + excludedCount - count; //부족한 수량을 다음 load의 파라미터로 사용
                                this.loadItemsQueue++;
                            }
                        }else if( excludedCount > 0  ){ //제외된 수량을 다음 load의 파라미터로 사용
                            this.loadItemsQueue++;
                        }
                        if (json.data.length<count){
                            this.isItemLoaded = true;
                        }else{
                            if (this.loadItemsQueue>0){
                                this.loadItemsQueue--;
                                this.loadSelectItems( addToNext, excludedCount );
                            }
                        }
                    }else{
                        this.isItemLoaded = true;
                        this.loaddingItems = false;
                    }
                    if( this.afterLoadSelectItem )this.afterLoadSelectItem();
                }.bind(this));
            }else{
                if (addToNext) this.loadItemsQueue++;
            }
        }
    },
    // getLastLoadedItemId: function(){
    //     if( this.tailExcludeItemId )return this.tailExcludeItemId;
    //     return (this.items.length) ? this.items[this.items.length-1].data.distinguishedName : "(0)";
    // },
    _getChildrenItemIds: function(data){
        return data.personList;
    },
    _newItemCategory: function(type, data, selector, item){
        return new MWF.xApplication.Selector.PersonWithController[type](data, selector, item)
    },

    _listItemByKey: function(callback, failure, key){
        o2.Actions.load("x_organization_assemble_control").PersonAction.listFilterPaging(1, 1000, {
            controller: true,
            key: key
        }, function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure);
    },
    _getItem: function(callback, failure, id, async){
        this.orgAction.getPerson(function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure, ((typeOf(id)==="string") ? id : id.distinguishedName), async);
    },
    _newItemSelected: function(data, selector, item, selectedNode){
        return new MWF.xApplication.Selector.PersonWithController.ItemSelected(data, selector, item, selectedNode)
    },
    _listItemByPinyin: function(callback, failure, key){
        o2.Actions.load("x_organization_assemble_control").PersonAction.listFilterPaging(1, 1000, {
            controller: true,
            key: key
        }, function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this), failure);
    },
    _newItem: function(data, selector, container){
        return new MWF.xApplication.Selector.PersonWithController.Item(data, selector, container);
    },
    _newItemSearch: function(data, selector, container){
        return this._newItem(data, selector, container);
    },
    _listItemNext: function(page, count, callback){
        o2.Actions.load("x_organization_assemble_control").PersonAction.listFilterPaging(page, count, {
            controller: true
        }, function(json){
            if (callback) callback.apply(this, [json]);
        }.bind(this));
        // this.orgAction.listPersonNext(page, count, function(json){
        //     if (callback) callback.apply(this, [json]);
        // }.bind(this));
    }
});

MWF.xApplication.Selector.PersonWithController.Item = new Class({
    Extends: MWF.xApplication.Selector.Person.Item
});

MWF.xApplication.Selector.PersonWithController.ItemSelected = new Class({
    Extends: MWF.xApplication.Selector.Person.ItemSelected
});

MWF.xApplication.Selector.PersonWithController.ItemCategory = new Class({
    Extends: MWF.xApplication.Selector.Person.ItemCategory
});

MWF.xApplication.Selector.PersonWithController.ItemGroupCategory = new Class({
    Extends: MWF.xApplication.Selector.Person.ItemGroupCategory
});

MWF.xApplication.Selector.PersonWithController.ItemRoleCategory = new Class({
    Extends: MWF.xApplication.Selector.Person.ItemRoleCategory
});

MWF.xApplication.Selector.PersonWithController.Filter = new Class({
    Extends: MWF.xApplication.Selector.Person.Filter,
    filter: function(value, callback){
        this.value = value;
        var key = this.value;

        if (this.options.groups.length || this.options.roles.length) key = {"key": key, "groupList": this.options.groupList, "roleList": this.options.roleList};
        this.orgAction.listPersonByKey(function(json){
            data = json.data;
            if (callback) callback(data)
        }.bind(this), null, key);
    }
});
