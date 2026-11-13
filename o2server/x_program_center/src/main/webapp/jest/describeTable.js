var Describe = function() {
  // 20180730
}
Describe.splitValue = function(str) {
  if (str) {
    if (str.length > 0) {
      return str.split(',');
    }
  }
  return [];
}
Describe.joinValue = function(o, split) {
  var s = ',';
  if (split) {
    s = '' + split;
  }
  if (o) {
    if (toString.apply(o) === '[object Array]') {
      return o.join(s);
    }
  }
  return o;
}
Describe.getUrlParam = function(name) {
  var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
  var r = window.location.search.substr(1).match(reg);
  if (r != null) return unescape(r[2]); return null; //반환 파라미터 값
}

Describe.prototype = {
  "load" : function() {
    var str = '<ul>';
    //var url = '../describe/table/x_calendar_core_entity.json';
    var url = '../describe/table/'+ Describe.getUrlParam("param");
    $.getJSON(url+'?rd=' + Math.random(), function(json) {
      Describe.json = json;
      $.each(json.tables, function(ji, j) {
        str += '<li xtype="menu" ' + 'style="margin-top: 30px;font-size:14px;font-weight:bold;"title="' +'" ><a id ="' + j.tableName + '_' + j.moduleName + '" href="#">' + "테이블:" +j.tableName + ' </a><span style="font-style:italic">(클래스:' + j.moduleName+ ')</span>';
        str += '</li>'
      });
      str += '</ul>';
      $("#menu").html(str);

      $.each(json.tables, function(mi, m) {
        $('#' + m.tableName + '_' + m.moduleName).click(
            function() {
              $('#result').html('');
              var txt = "";
              txt += '<table style="table-layout:word-wrap:break-word;word-break:break-all;">';
              txt += '<tr style="height:28px"><td width ="5%"><b>번호</b></td><td width ="20%"><b>열 이름</b></td><td style="word-wrap:break-word;word-break:break-all;width:20%"><b>유형</b></td><td width ="10%"><b>길이</b></td><td width ="*"><b>용도</b></td></tr>';
              $.each(m.columnProperty, function(ci, c) {
                if(c.type == "ContainerTable"){
                  txt += '<tr><td>'+(ci+1)+'</td><td>'+c.name+'</td><td >'+c.type+'</td><td>'+c.length+'</td><td>'+c.remark+ "("+ c.containerTableProperty.name+')</td><td>'
                  txt +='</td></tr>';
                  txt += '<tr><td></td><td colspan="4"><table style="width: 600px;font-style:italic"><tr><td width ="5%"><b>번호</b></td><td width ="30%"><b>열 이름</b></td><td width ="20%"><b>유형</b></td><td width ="*"><b>용도</b></td></tr>';
                  $.each(c.containerTableProperty.containerTableColumnProperty, function(cti, ct) {
                    txt += '<tr><td>'+(cti+1)+'</td><td>'+ct.name+'</td><td>'+ct.type+'</td><td>'+ct.remark+'</td></tr>';
                  });

                  txt +='</table></td></tr>';

                }else{
                  txt += '<tr  style="height:28px"><td>'+(ci+1)+'</td><td>'+c.name+'</td><td style="word-wrap:break-word;word-break:break-all;">'+c.type+'</td><td>'+c.length+'</td><td>'+c.remark+'</td><td></td></tr>';
                }
              });

              txt += '</table>';
              $('#result').html(txt);
            });

      });

      $("[xtype='menu']").click(
          function(event) {
            if(event.stopPropagation){
              event.stopPropagation();
            }else{
              event.cancelBubble = true;
            }
            $(this).children().each(function(i){
              debugger;
              //if(this.tagName != "SPAN"){
              //$(this).toggle();
              //}
            });
          });
      $("[xtype='li']").click( function(event) {
        if(event.stopPropagation){
          event.stopPropagation();
        }else{
          event.cancelBubble = true;
        }
      })
      $("[xtype='menu']").each(function(i){
            if(i!=0){
              $(this).children().each(function(i){

                //if(this.tagName != "SPAN"){
                //$(this).toggle();
                //}
              });
            }
          }
      );
    });
  }
}
