var GLOBAL_ITEMS=function(){var t=[];for(var e in this)t.push(e);return t}();!function(){this.MooTools={version:"1.5.0dev",build:"%build%"};var t=this.typeOf=function(t){if(null==t)return"null";if(null!=t.$family)return t.$family();if(t.nodeName){if(1==t.nodeType)return"element";if(3==t.nodeType)return/\S/.test(t.nodeValue)?"textnode":"whitespace"}else if("number"==typeof t.length){if(t.callee)return"arguments";if("item"in t)return"collection"}return typeof t},e=(this.instanceOf=function(t,e){if(null==t)return!1;for(var n=t.$constructor||t.constructor;n;){if(n===e)return!0;n=n.parent}return!!t.hasOwnProperty&&t instanceof e},this.Function),n=!0;for(var r in{toString:1})n=null;n&&(n=["hasOwnProperty","valueOf","isPrototypeOf","propertyIsEnumerable","toLocaleString","toString","constructor"]),e.prototype.overloadSetter=function(t){var e=this;return function(r,i){if(null==r)return this;if(t||"string"!=typeof r){for(var a in r)e.call(this,a,r[a]);if(n)for(var o=n.length;o--;)a=n[o],r.hasOwnProperty(a)&&e.call(this,a,r[a])}else e.call(this,r,i);return this}},e.prototype.overloadGetter=function(t){var e=this;return function(n){var r,i;if("string"!=typeof n?r=n:arguments.length>1?r=arguments:t&&(r=[n]),r){i={};for(var a=0;a<r.length;a++)i[r[a]]=e.call(this,r[a])}else i=e.call(this,n);return i}},e.prototype.extend=function(t,e){this[t]=e}.overloadSetter(),e.prototype.implement=function(t,e){this.prototype[t]=e}.overloadSetter();var i=Array.prototype.slice;e.from=function(e){return"function"==t(e)?e:function(){return e}},e.convert=e.from,Array.from=function(e){return null==e?[]:a.isEnumerable(e)&&"string"!=typeof e?"array"==t(e)?e:i.call(e):[e]},Array.convert=Array.from,Number.from=function(t){var e=parseFloat(t);return isFinite(e)?e:null},Number.convert=Number.from,String.from=function(t){return t+""},String.convert=String.from,e.implement({hide:function(){return this.$hidden=!0,this},protect:function(){return this.$protected=!0,this}});var a=this.Type=function(e,n){if(e){var r=e.toLowerCase();a["is"+e]=function(e){return t(e)==r},null!=n&&(n.prototype.$family=function(){return r}.hide())}return null==n?null:(n.extend(this),n.$constructor=a,n.prototype.$constructor=n,n)},o=Object.prototype.toString;a.isEnumerable=function(t){return null!=t&&"number"==typeof t.length&&"[object Function]"!=o.call(t)};var s={},u=function(e){var n=t(e.prototype);return s[n]||(s[n]=[])},c=function(e,n){if(!n||!n.$hidden){for(var r=u(this),a=0;a<r.length;a++){var o=r[a];"type"==t(o)?c.call(o,e,n):o.call(this,e,n)}var s=this.prototype[e];null!=s&&s.$protected||(this.prototype[e]=n),null==this[e]&&"function"==t(n)&&l.call(this,e,(function(t){return n.apply(t,i.call(arguments,1))}))}},l=function(t,e){if(!e||!e.$hidden){var n=this[t];null!=n&&n.$protected||(this[t]=e)}};a.implement({implement:c.overloadSetter(),extend:l.overloadSetter(),alias:function(t,e){c.call(this,t,this.prototype[e])}.overloadSetter(),mirror:function(t){return u(this).push(t),this}}),new a("Type",a);var h=function(t,e,n){var r=e!=Object,i=e.prototype;r&&(e=new a(t,e));for(var o=0,s=n.length;o<s;o++){var u=n[o],c=e[u],l=i[u];c&&c.protect(),r&&l&&e.implement(u,l.protect())}if(r){var f=i.propertyIsEnumerable(n[0]);e.forEachMethod=function(t){if(!f)for(var e=0,r=n.length;e<r;e++)t.call(i,i[n[e]],n[e]);for(var a in i)t.call(i,i[a],a)}}return h};h("String",String,["charAt","charCodeAt","concat","indexOf","lastIndexOf","match","quote","replace","search","slice","split","substr","substring","trim","toLowerCase","toUpperCase"])("Array",Array,["pop","push","reverse","shift","sort","splice","unshift","concat","join","slice","indexOf","lastIndexOf","filter","forEach","every","map","some","reduce","reduceRight"])("Number",Number,["toExponential","toFixed","toLocaleString","toPrecision"])("Function",e,["apply","call","bind"])("RegExp",RegExp,["exec","test"])("Object",Object,["create","defineProperty","defineProperties","keys","getPrototypeOf","getOwnPropertyDescriptor","getOwnPropertyNames","preventExtensions","isExtensible","seal","isSealed","freeze","isFrozen"])("Date",Date,["now"]),Object.extend=l.overloadSetter(),Date.extend("now",(function(){return+new Date})),new a("Boolean",Boolean),Number.prototype.$family=function(){return isFinite(this)?"number":"null"}.hide(),Number.extend("random",(function(t,e){return Math.floor(Math.random()*(e-t+1)+t)}));var f=Object.prototype.hasOwnProperty;Object.extend("forEach",(function(t,e,n){for(var r in t)f.call(t,r)&&e.call(n,t[r],r,t)})),Object.each=Object.forEach,Array.implement({forEach:function(t,e){for(var n=0,r=this.length;n<r;n++)n in this&&t.call(e,this[n],n,this)},each:function(t,e){return Array.forEach(this,t,e),this}});var d=function(e){switch(t(e)){case"array":return e.clone();case"object":return Object.clone(e);default:return e}};Array.implement("clone",(function(){for(var t=this.length,e=new Array(t);t--;)e[t]=d(this[t]);return e}));var g=function(e,n,r){switch(t(r)){case"object":"object"==t(e[n])?Object.merge(e[n],r):e[n]=Object.clone(r);break;case"array":e[n]=r.clone();break;default:e[n]=r}return e};Object.extend({merge:function(e,n,r){if("string"==t(n))return g(e,n,r);for(var i=1,a=arguments.length;i<a;i++){var o=arguments[i];for(var s in o)g(e,s,o[s])}return e},clone:function(t){var e={};for(var n in t)e[n]=d(t[n]);return e},append:function(t){for(var e=1,n=arguments.length;e<n;e++){var r=arguments[e]||{};for(var i in r)t[i]=r[i]}return t}}),["Object","WhiteSpace","TextNode","Collection","Arguments"].each((function(t){new a(t)}));var p=Date.now();String.extend("uniqueID",(function(){return(p++).toString(36)}))}(),Array.implement({every:function(t,e){for(var n=0,r=this.length>>>0;n<r;n++)if(n in this&&!t.call(e,this[n],n,this))return!1;return!0},filter:function(t,e){for(var n,r=[],i=0,a=this.length>>>0;i<a;i++)i in this&&(n=this[i],t.call(e,n,i,this)&&r.push(n));return r},indexOf:function(t,e){for(var n=this.length>>>0,r=e<0?Math.max(0,n+e):e||0;r<n;r++)if(this[r]===t)return r;return-1},map:function(t,e){for(var n=this.length>>>0,r=Array(n),i=0;i<n;i++)i in this&&(r[i]=t.call(e,this[i],i,this));return r},some:function(t,e){for(var n=0,r=this.length>>>0;n<r;n++)if(n in this&&t.call(e,this[n],n,this))return!0;return!1},clean:function(){return this.filter((function(t){return null!=t}))},invoke:function(t){var e=Array.slice(arguments,1);return this.map((function(n){return n[t].apply(n,e)}))},associate:function(t){for(var e={},n=Math.min(this.length,t.length),r=0;r<n;r++)e[t[r]]=this[r];return e},link:function(t){for(var e={},n=0,r=this.length;n<r;n++)for(var i in t)if(t[i](this[n])){e[i]=this[n],delete t[i];break}return e},contains:function(t,e){return-1!=this.indexOf(t,e)},append:function(t){return this.push.apply(this,t),this},getLast:function(){return this.length?this[this.length-1]:null},getRandom:function(){return this.length?this[Number.random(0,this.length-1)]:null},include:function(t){return this.contains(t)||this.push(t),this},combine:function(t){for(var e=0,n=t.length;e<n;e++)this.include(t[e]);return this},erase:function(t){for(var e=this.length;e--;)this[e]===t&&this.splice(e,1);return this},empty:function(){return this.length=0,this},flatten:function(){for(var t=[],e=0,n=this.length;e<n;e++){var r=typeOf(this[e]);"null"!=r&&(t=t.concat("array"==r||"collection"==r||"arguments"==r||instanceOf(this[e],Array)?Array.flatten(this[e]):this[e]))}return t},pick:function(){for(var t=0,e=this.length;t<e;t++)if(null!=this[t])return this[t];return null},hexToRgb:function(t){if(3!=this.length)return null;var e=this.map((function(t){return 1==t.length&&(t+=t),t.toInt(16)}));return t?e:"rgb("+e+")"},rgbToHex:function(t){if(this.length<3)return null;if(4==this.length&&0==this[3]&&!t)return"transparent";for(var e=[],n=0;n<3;n++){var r=(this[n]-0).toString(16);e.push(1==r.length?"0"+r:r)}return t?e:"#"+e.join("")}}),String.implement({test:function(t,e){return("regexp"==typeOf(t)?t:new RegExp(""+t,e)).test(this)},contains:function(t,e){return e?(e+this+e).indexOf(e+t+e)>-1:String(this).indexOf(t)>-1},trim:function(){return String(this).replace(/^\s+|\s+$/g,"")},clean:function(){return String(this).replace(/\s+/g," ").trim()},camelCase:function(){return String(this).replace(/-\D/g,(function(t){return t.charAt(1).toUpperCase()}))},hyphenate:function(){return String(this).replace(/[A-Z]/g,(function(t){return"-"+t.charAt(0).toLowerCase()}))},capitalize:function(){return String(this).replace(/\b[a-z]/g,(function(t){return t.toUpperCase()}))},escapeRegExp:function(){return String(this).replace(/([-.*+?^${}()|[\]\/\\])/g,"\\$1")},toInt:function(t){return parseInt(this,t||10)},toFloat:function(){return parseFloat(this)},hexToRgb:function(t){var e=String(this).match(/^#?(\w{1,2})(\w{1,2})(\w{1,2})$/);return e?e.slice(1).hexToRgb(t):null},rgbToHex:function(t){var e=String(this).match(/\d{1,3}/g);return e?e.rgbToHex(t):null},substitute:function(t,e){return String(this).replace(e||/\\?\{([^{}]+)\}/g,(function(e,n){return"\\"==e.charAt(0)?e.slice(1):null!=t[n]?t[n]:""}))}}),Function.extend({attempt:function(){for(var t=0,e=arguments.length;t<e;t++)try{return arguments[t]()}catch(t){}return null}}),Function.implement({attempt:function(t,e){try{return this.apply(e,Array.from(t))}catch(t){}return null},bind:function(t){var e=this,n=arguments.length>1?Array.slice(arguments,1):null,r=function(){},i=function(){var a=t,o=arguments.length;this instanceof i&&(r.prototype=e.prototype,a=new r);var s=n||o?e.apply(a,n&&o?n.concat(Array.slice(arguments)):n||arguments):e.call(a);return a==t?s:a};return i},pass:function(t,e){var n=this;return null!=t&&(t=Array.from(t)),function(){return n.apply(e,t||arguments)}},delay:function(t,e,n){return setTimeout(this.pass(null==n?[]:n,e),t)},periodical:function(t,e,n){return setInterval(this.pass(null==n?[]:n,e),t)}}),Number.implement({limit:function(t,e){return Math.min(e,Math.max(t,this))},round:function(t){return t=Math.pow(10,t||0).toFixed(t<0?-t:0),Math.round(this*t)/t},times:function(t,e){for(var n=0;n<this;n++)t.call(e,n,this)},toFloat:function(){return parseFloat(this)},toInt:function(t){return parseInt(this,t||10)}}),Number.alias("each","times"),function(t){var e={};["abs","acos","asin","atan","atan2","ceil","cos","exp","floor","log","max","min","pow","sin","sqrt","tan"].each((function(t){Number[t]||(e[t]=function(){return Math[t].apply(null,[this].concat(Array.from(arguments)))})})),Number.implement(e)}(),function(){var t=this.Class=new Type("Class",(function(r){instanceOf(r,Function)&&(r={initialize:r});var i=function(){if(n(this),i.$prototyping)return this;this.$caller=null;var t=this.initialize?this.initialize.apply(this,arguments):this;return this.$caller=this.caller=null,t}.extend(this).implement(r);return i.$constructor=t,i.prototype.$constructor=i,i.prototype.parent=e,i})),e=function(){if(!this.$caller)throw new Error('The method "parent" cannot be called.');var t=this.$caller.$name,e=this.$caller.$owner.parent,n=e?e.prototype[t]:null;if(!n)throw new Error('The method "'+t+'" has no parent.');return n.apply(this,arguments)},n=function(t){for(var e in t){var r=t[e];switch(typeOf(r)){case"object":var i=function(){};i.prototype=r,t[e]=n(new i);break;case"array":t[e]=r.clone()}}return t},r=function(e,n,r){if(t.Mutators.hasOwnProperty(e)&&null==(n=t.Mutators[e].call(this,n)))return this;if("function"==typeOf(n)){if(n.$hidden)return this;this.prototype[e]=r?n:function(t,e,n){n.$origin&&(n=n.$origin);var r=function(){if(n.$protected&&null==this.$caller)throw new Error('The method "'+e+'" cannot be called.');var t=this.caller,i=this.$caller;this.caller=i,this.$caller=r;var a=n.apply(this,arguments);return this.$caller=i,this.caller=t,a}.extend({$owner:t,$origin:n,$name:e});return r}(this,e,n)}else Object.merge(this.prototype,e,n);return this};t.implement("implement",r.overloadSetter()),t.Mutators={Extends:function(t){this.parent=t,this.prototype=function(t){t.$prototyping=!0;var e=new t;return delete t.$prototyping,e}(t)},Implements:function(t){Array.from(t).each((function(t){var e=new t;for(var n in e)r.call(this,n,e[n],!0)}),this)}}}(),function(){this.Chain=new Class({$chain:[],chain:function(){return this.$chain.append(Array.flatten(arguments)),this},callChain:function(){return!!this.$chain.length&&this.$chain.shift().apply(this,arguments)},clearChain:function(){return this.$chain.empty(),this}});var t=function(t){return t.replace(/^on([A-Z])/,(function(t,e){return e.toLowerCase()}))};this.Events=new Class({$events:{},addEvent:function(e,n,r){return e=t(e),this.$events[e]=(this.$events[e]||[]).include(n),r&&(n.internal=!0),this},addEvents:function(t){for(var e in t)this.addEvent(e,t[e]);return this},fireEvent:function(e,n,r){e=t(e);var i=this.$events[e];return i?(n=Array.from(n),i.each((function(t){r?t.delay(r,this,n):t.apply(this,n)}),this),this):this},removeEvent:function(e,n){e=t(e);var r=this.$events[e];if(r&&!n.internal){var i=r.indexOf(n);-1!=i&&delete r[i]}return this},removeEvents:function(e){var n;if("object"==typeOf(e)){for(n in e)this.removeEvent(n,e[n]);return this}for(n in e&&(e=t(e)),this.$events)if(!e||e==n)for(var r=this.$events[n],i=r.length;i--;)i in r&&this.removeEvent(n,r[i]);return this}}),this.Options=new Class({setOptions:function(){var t=this.options=Object.merge.apply(null,[{},this.options].append(arguments));if(this.addEvent)for(var e in t)"function"==typeOf(t[e])&&/^on[A-Z]/.test(e)&&(this.addEvent(e,t[e]),delete t[e]);return this}})}(),function(){var t=Object.prototype.hasOwnProperty;Object.extend({subset:function(t,e){for(var n={},r=0,i=e.length;r<i;r++){var a=e[r];a in t&&(n[a]=t[a])}return n},map:function(e,n,r){var i={};for(var a in e)t.call(e,a)&&(i[a]=n.call(r,e[a],a,e));return i},filter:function(e,n,r){var i={};for(var a in e){var o=e[a];t.call(e,a)&&n.call(r,o,a,e)&&(i[a]=o)}return i},every:function(e,n,r){for(var i in e)if(t.call(e,i)&&!n.call(r,e[i],i))return!1;return!0},some:function(e,n,r){for(var i in e)if(t.call(e,i)&&n.call(r,e[i],i))return!0;return!1},keys:function(e){var n=[];for(var r in e)t.call(e,r)&&n.push(r);return n},values:function(e){var n=[];for(var r in e)t.call(e,r)&&n.push(e[r]);return n},getLength:function(t){return Object.keys(t).length},keyOf:function(e,n){for(var r in e)if(t.call(e,r)&&e[r]===n)return r;return null},contains:function(t,e){return null!=Object.keyOf(t,e)},toQueryString:function(t,e){var n=[];return Object.each(t,(function(t,r){var i;switch(e&&(r=e+"["+r+"]"),typeOf(t)){case"object":i=Object.toQueryString(t,r);break;case"array":var a={};t.each((function(t,e){a[e]=t})),i=Object.toQueryString(a,r);break;default:i=r+"="+encodeURIComponent(t)}null!=t&&n.push(i)})),n.join("&")}})}(),"undefined"!=typeof exports&&function(){for(var t in this)GLOBAL_ITEMS.contains(t)||(exports[t]=this[t]);exports.apply=function(t){Object.append(t,exports)}}(),MooTools.More={version:"1.6.1-dev",build:"%build%"},function(){var t=function(t){return null!=t},e=Object.prototype.hasOwnProperty;Object.extend({getFromPath:function(t,n){"string"==typeof n&&(n=n.split("."));for(var r=0,i=n.length;r<i;r++){if(!e.call(t,n[r]))return null;t=t[n[r]]}return t},cleanValues:function(e,n){for(var r in n=n||t,e)n(e[r])||delete e[r];return e},erase:function(t,n){return e.call(t,n)&&delete t[n],t},run:function(t){var e=Array.slice(arguments,1);for(var n in t)t[n].apply&&t[n].apply(t,e);return t}})}(),function(){var t=null,e={},n=function(t){return instanceOf(t,r.Set)?t:e[t]},r=this.Locale={define:function(n,i,a,o){var s;return instanceOf(n,r.Set)?(s=n.name)&&(e[s]=n):(e[s=n]||(e[s]=new r.Set(s)),n=e[s]),i&&n.define(i,a,o),t||(t=n),n},use:function(e){return(e=n(e))&&(t=e,this.fireEvent("change",e)),this},getCurrent:function(){return t},get:function(e,n){return t?t.get(e,n):""},inherit:function(t,e,r){return(t=n(t))&&t.inherit(e,r),this},list:function(){return Object.keys(e)}};Object.append(r,new Events),r.Set=new Class({sets:{},inherits:{locales:[],sets:{}},initialize:function(t){this.name=t||""},define:function(t,e,n){var r=this.sets[t];return r||(r={}),e&&("object"==typeOf(e)?r=Object.merge(r,e):r[e]=n),this.sets[t]=r,this},get:function(t,n,r){var i=Object.getFromPath(this.sets,t);if(null!=i){var a=typeOf(i);return"function"==a?i=i.apply(null,Array.convert(n)):"object"==a&&(i=Object.clone(i)),i}var o=t.indexOf("."),s=o<0?t:t.substr(0,o),u=(this.inherits.sets[s]||[]).combine(this.inherits.locales).include("en-US");r||(r=[]);for(var c=0,l=u.length;c<l;c++)if(!r.contains(u[c])){r.include(u[c]);var h=e[u[c]];if(h&&null!=(i=h.get(t,n,r)))return i}return""},inherit:function(t,e){t=Array.convert(t),e&&!this.inherits.sets[e]&&(this.inherits.sets[e]=[]);for(var n=t.length;n--;)(e?this.inherits.sets[e]:this.inherits.locales).unshift(t[n]);return this}})}(),Locale.define("en-US","Date",{months:["January","February","March","April","May","June","July","August","September","October","November","December"],months_abbr:["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"],days:["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"],days_abbr:["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],dateOrder:["month","date","year"],shortDate:"%m/%d/%Y",shortTime:"%I:%M%p",AM:"AM",PM:"PM",firstDayOfWeek:0,ordinal:function(t){return t>3&&t<21?"th":["th","st","nd","rd","th"][Math.min(t%10,4)]},lessThanMinuteAgo:"less than a minute ago",minuteAgo:"about a minute ago",minutesAgo:"{delta} minutes ago",hourAgo:"about an hour ago",hoursAgo:"about {delta} hours ago",dayAgo:"1 day ago",daysAgo:"{delta} days ago",weekAgo:"1 week ago",weeksAgo:"{delta} weeks ago",monthAgo:"1 month ago",monthsAgo:"{delta} months ago",yearAgo:"1 year ago",yearsAgo:"{delta} years ago",lessThanMinuteUntil:"less than a minute from now",minuteUntil:"about a minute from now",minutesUntil:"{delta} minutes from now",hourUntil:"about an hour from now",hoursUntil:"about {delta} hours from now",dayUntil:"1 day from now",daysUntil:"{delta} days from now",weekUntil:"1 week from now",weeksUntil:"{delta} weeks from now",monthUntil:"1 month from now",monthsUntil:"{delta} months from now",yearUntil:"1 year from now",yearsUntil:"{delta} years from now"}),function(){var t=this.Date,e=t.Methods={ms:"Milliseconds",year:"FullYear",min:"Minutes",mo:"Month",sec:"Seconds",hr:"Hours"};["Date","Day","FullYear","Hours","Milliseconds","Minutes","Month","Seconds","Time","TimezoneOffset","Week","Timezone","GMTOffset","DayOfYear","LastMonth","LastDayOfMonth","UTCDate","UTCDay","UTCFullYear","AMPM","Ordinal","UTCHours","UTCMilliseconds","UTCMinutes","UTCMonth","UTCSeconds","UTCMilliseconds"].each((function(e){t.Methods[e.toLowerCase()]=e}));var n=function(t,e,r){return 1==e?t:t<Math.pow(10,e-1)?(r||"0")+n(t,e-1,r):t};t.implement({set:function(t,n){t=t.toLowerCase();var r=e[t]&&"set"+e[t];return r&&this[r]&&this[r](n),this}.overloadSetter(),get:function(t){t=t.toLowerCase();var n=e[t]&&"get"+e[t];return n&&this[n]?this[n]():null}.overloadGetter(),clone:function(){return new t(this.get("time"))},increment:function(e,n){switch(n=null!=n?n:1,e=e||"day"){case"year":return this.increment("month",12*n);case"month":var r=this.get("date");return this.set("date",1).set("mo",this.get("mo")+n),this.set("date",r.min(this.get("lastdayofmonth")));case"week":return this.increment("day",7*n);case"day":return this.set("date",this.get("date")+n)}if(!t.units[e])throw new Error(e+" is not a supported interval");return this.set("time",this.get("time")+n*t.units[e]())},decrement:function(t,e){return this.increment(t,-1*(null!=e?e:1))},isLeapYear:function(){return t.isLeapYear(this.get("year"))},clearTime:function(){return this.set({hr:0,min:0,sec:0,ms:0})},diff:function(e,n){return"string"==typeOf(e)&&(e=t.parse(e)),((e-this)/t.units[n||"day"](3,3)).round()},getLastDayOfMonth:function(){return t.daysInMonth(this.get("mo"),this.get("year"))},getDayOfYear:function(){return(t.UTC(this.get("year"),this.get("mo"),this.get("date")+1)-t.UTC(this.get("year"),0,1))/t.units.day()},setDay:function(e,n){null==n&&""===(n=t.getMsg("firstDayOfWeek"))&&(n=1),e=(7+t.parseDay(e,!0)-n)%7;var r=(7+this.get("day")-n)%7;return this.increment("day",e-r)},getWeek:function(e){null==e&&""===(e=t.getMsg("firstDayOfWeek"))&&(e=1);var n,r=this,i=(7+r.get("day")-e)%7,a=0;if(1==e){var o=r.get("month"),s=r.get("date")-i;if(11==o&&s>28)return 1;0==o&&s<-2&&(r=new t(r).decrement("day",i),i=0),(n=new t(r.get("year"),0,1).get("day")||7)>4&&(a=-7)}else n=new t(r.get("year"),0,1).get("day");return a+=r.get("dayofyear"),a+=6-i,(a+=(7+n-e)%7)/7},getOrdinal:function(e){return t.getMsg("ordinal",e||this.get("date"))},getTimezone:function(){return this.toString().replace(/^.*? ([A-Z]{3}).[0-9]{4}.*$/,"$1").replace(/^.*?\(([A-Z])[a-z]+ ([A-Z])[a-z]+ ([A-Z])[a-z]+\)$/,"$1$2$3")},getGMTOffset:function(){var t=this.get("timezoneOffset");return(t>0?"-":"+")+n((t.abs()/60).floor(),2)+n(t%60,2)},setAMPM:function(t){t=t.toUpperCase();var e=this.get("hr");return e>11&&"AM"==t?this.decrement("hour",12):e<12&&"PM"==t?this.increment("hour",12):this},getAMPM:function(){return this.get("hr")<12?"AM":"PM"},parse:function(e){return this.set("time",t.parse(e)),this},isValid:function(t){return t||(t=this),"date"==typeOf(t)&&!isNaN(t.valueOf())},format:function(e){if(!this.isValid())return"invalid date";if(e||(e="%x %X"),"string"==typeof e&&(e=a[e.toLowerCase()]||e),"function"==typeof e)return e(this);var r=this;return e.replace(/%([a-z%])/gi,(function(e,i){switch(i){case"a":return t.getMsg("days_abbr")[r.get("day")];case"A":return t.getMsg("days")[r.get("day")];case"b":return t.getMsg("months_abbr")[r.get("month")];case"B":return t.getMsg("months")[r.get("month")];case"c":return r.format("%a %b %d %H:%M:%S %Y");case"d":return n(r.get("date"),2);case"e":return n(r.get("date"),2," ");case"H":return n(r.get("hr"),2);case"I":return n(r.get("hr")%12||12,2);case"j":return n(r.get("dayofyear"),3);case"k":return n(r.get("hr"),2," ");case"l":return n(r.get("hr")%12||12,2," ");case"L":return n(r.get("ms"),3);case"m":return n(r.get("mo")+1,2);case"M":return n(r.get("min"),2);case"o":return r.get("ordinal");case"p":return t.getMsg(r.get("ampm"));case"s":return Math.round(r/1e3);case"S":return n(r.get("seconds"),2);case"T":return r.format("%H:%M:%S");case"U":return n(r.get("week"),2);case"w":return r.get("day");case"x":return r.format(t.getMsg("shortDate"));case"X":return r.format(t.getMsg("shortTime"));case"y":return r.get("year").toString().substr(2);case"Y":return r.get("year");case"z":return r.get("GMTOffset");case"Z":return r.get("Timezone")}return i}))},toISOString:function(){return this.format("iso8601")}}).alias({toJSON:"toISOString",compare:"diff",strftime:"format"});var r=["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],i=["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"],a={db:"%Y-%m-%d %H:%M:%S",compact:"%Y%m%dT%H%M%S",short:"%d %b %H:%M",long:"%B %d, %Y %H:%M",rfc822:function(t){return r[t.get("day")]+t.format(", %d ")+i[t.get("month")]+t.format(" %Y %H:%M:%S %Z")},rfc2822:function(t){return r[t.get("day")]+t.format(", %d ")+i[t.get("month")]+t.format(" %Y %H:%M:%S %z")},iso8601:function(t){return t.getUTCFullYear()+"-"+n(t.getUTCMonth()+1,2)+"-"+n(t.getUTCDate(),2)+"T"+n(t.getUTCHours(),2)+":"+n(t.getUTCMinutes(),2)+":"+n(t.getUTCSeconds(),2)+"."+n(t.getUTCMilliseconds(),3)+"Z"}},o=[],s=t.parse,u=function(e,n,r){var i=-1,a=t.getMsg(e+"s");switch(typeOf(n)){case"object":i=a[n.get(e)];break;case"number":if(!(i=a[n]))throw new Error("Invalid "+e+" index: "+n);break;case"string":var o=a.filter((function(t){return this.test(t)}),new RegExp("^"+n,"i"));if(!o.length)throw new Error("Invalid "+e+" string");if(o.length>1)throw new Error("Ambiguous "+e);i=o[0]}return r?a.indexOf(i):i},c=1900,l=70;t.extend({getMsg:function(t,e){return Locale.get("Date."+t,e)},units:{ms:Function.convert(1),second:Function.convert(1e3),minute:Function.convert(6e4),hour:Function.convert(36e5),day:Function.convert(864e5),week:Function.convert(6084e5),month:function(e,n){var r=new t;return 864e5*t.daysInMonth(null!=e?e:r.get("mo"),null!=n?n:r.get("year"))},year:function(e){return e=e||(new t).get("year"),t.isLeapYear(e)?316224e5:31536e6}},daysInMonth:function(e,n){return[31,t.isLeapYear(n)?29:28,31,30,31,30,31,31,30,31,30,31][e]},isLeapYear:function(t){return t%4==0&&t%100!=0||t%400==0},parse:function(e){var n,r=typeOf(e);return"number"==r?new t(e):"string"!=r?e:(e=e.clean()).length?(o.some((function(t){var r=t.re.exec(e);return!!r&&(n=t.handler(r))})),n&&n.isValid()||(n=new t(s(e)))&&n.isValid()||(n=new t(e.toInt())),n):null},parseDay:function(t,e){return u("day",t,e)},parseMonth:function(t,e){return u("month",t,e)},parseUTC:function(e){var n=new t(e),r=t.UTC(n.get("year"),n.get("mo"),n.get("date"),n.get("hr"),n.get("min"),n.get("sec"),n.get("ms"));return new t(r)},orderIndex:function(e){return t.getMsg("dateOrder").indexOf(e)+1},defineFormat:function(t,e){return a[t]=e,this},defineParser:function(t){return o.push(t.re&&t.handler?t:g(t)),this},defineParsers:function(){return Array.flatten(arguments).each(t.defineParser),this},define2DigitYearStart:function(t){return c=t-(l=t%100),this}}).extend({defineFormats:t.defineFormat.overloadSetter()});var h,f=function(e){return new RegExp("(?:"+t.getMsg(e).map((function(t){return t.substr(0,3)})).join("|")+")[a-z]*")},d={d:/[0-2]?[0-9]|3[01]/,H:/[01]?[0-9]|2[0-3]/,I:/0?[1-9]|1[0-2]/,M:/[0-5]?\d/,s:/\d+/,o:/[a-z]*/,p:/[ap]\.?m\.?/,y:/\d{2}|\d{4}/,Y:/\d{4}/,z:/Z|[+-]\d{2}(?::?\d{2})?/};d.m=d.I,d.S=d.M;var g=function(e){if(!h)return{format:e};var n=[],r=(e.source||e).replace(/%([a-z])/gi,(function(e,n){return function(e){switch(e){case"T":return"%H:%M:%S";case"x":return(1==t.orderIndex("month")?"%m[-./]%d":"%d[-./]%m")+"([-./]%y)?";case"X":return"%H([.:]%M)?([.:]%S([.:]%s)?)? ?%p? ?%z?"}return null}(n)||e})).replace(/\((?!\?)/g,"(?:").replace(/ (?!\?|\*)/g,",? ").replace(/%([a-z%])/gi,(function(t,e){var r=d[e];return r?(n.push(e),"("+r.source+")"):e})).replace(/\[a-z\]/gi,"[a-z\\u00c0-\\uffff;&]");return{format:e,re:new RegExp("^"+r+"$","i"),handler:function(e){e=e.slice(1).associate(n);var r=(new t).clearTime(),i=e.y||e.Y;for(var a in null!=i&&p.call(r,"y",i),"d"in e&&p.call(r,"d",1),("m"in e||e.b||e.B)&&p.call(r,"m",1),e)p.call(r,a,e[a]);return r}}},p=function(e,n){if(!n)return this;switch(e){case"a":case"A":return this.set("day",t.parseDay(n,!0));case"b":case"B":return this.set("mo",t.parseMonth(n,!0));case"d":return this.set("date",n);case"H":case"I":return this.set("hr",n);case"m":return this.set("mo",n-1);case"M":return this.set("min",n);case"p":return this.set("ampm",n.replace(/\./g,""));case"S":return this.set("sec",n);case"s":return this.set("ms",1e3*("0."+n));case"w":return this.set("day",n);case"Y":return this.set("year",n);case"y":return(n=+n)<100&&(n+=c+(n<l?100:0)),this.set("year",n);case"z":"Z"==n&&(n="+00");var r=n.match(/([+-])(\d{2}):?(\d{2})?/);return r=(r[1]+"1")*(60*r[2]+(+r[3]||0))+this.getTimezoneOffset(),this.set("time",this-6e4*r)}return this};t.defineParsers("%Y([-./]%m([-./]%d((T| )%X)?)?)?","%Y%m%d(T%H(%M%S?)?)?","%x( %X)?","%d%o( %b( %Y)?)?( %X)?","%b( %d%o)?( %Y)?( %X)?","%Y %b( %d%o( %X)?)?","%o %b %d %X %z %Y","%T","%H:%M( ?%p)?"),Locale.addEvent("change",(function(t){Locale.get("Date")&&function(t){h=t,d.a=d.A=f("days"),d.b=d.B=f("months"),o.each((function(t,e){t.format&&(o[e]=g(t.format))}))}(t)})).fireEvent("change",Locale.getCurrent())}(),Locale.define("zh-CHS","Date",{months:["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"],months_abbr:["一","二","三","四","五","六","七","八","九","十","十一","十二"],days:["星期日","星期一","星期二","星期三","星期四","星期五","星期六"],days_abbr:["日","一","二","三","四","五","六"],dateOrder:["year","month","date"],shortDate:"%Y-%m-%d",shortTime:"%I:%M%p",AM:"AM",PM:"PM",firstDayOfWeek:1,ordinal:"",lessThanMinuteAgo:"不到1分钟前",minuteAgo:"大约1分钟前",minutesAgo:"{delta}分钟之前",hourAgo:"大约1小时前",hoursAgo:"大约{delta}小时前",dayAgo:"1天前",daysAgo:"{delta}天前",weekAgo:"1星期前",weeksAgo:"{delta}星期前",monthAgo:"1个月前",monthsAgo:"{delta}个月前",yearAgo:"1年前",yearsAgo:"{delta}年前",lessThanMinuteUntil:"从现在开始不到1分钟",minuteUntil:"从现在开始約1分钟",minutesUntil:"从现在开始约{delta}分钟",hourUntil:"从现在开始1小时",hoursUntil:"从现在开始约{delta}小时",dayUntil:"从现在开始1天",daysUntil:"从现在开始{delta}天",weekUntil:"从现在开始1星期",weeksUntil:"从现在开始{delta}星期",monthUntil:"从现在开始一个月",monthsUntil:"从现在开始{delta}个月",yearUntil:"从现在开始1年",yearsUntil:"从现在开始{delta}年"}),Locale.define("zh-CHT","Date",{months:["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"],months_abbr:["一","二","三","四","五","六","七","八","九","十","十一","十二"],days:["星期日","星期一","星期二","星期三","星期四","星期五","星期六"],days_abbr:["日","一","二","三","四","五","六"],dateOrder:["year","month","date"],shortDate:"%Y-%m-%d",shortTime:"%I:%M%p",AM:"AM",PM:"PM",firstDayOfWeek:1,ordinal:"",lessThanMinuteAgo:"不到1分鐘前",minuteAgo:"大約1分鐘前",minutesAgo:"{delta}分鐘之前",hourAgo:"大約1小時前",hoursAgo:"大約{delta}小時前",dayAgo:"1天前",daysAgo:"{delta}天前",weekAgo:"1星期前",weeksAgo:"{delta}星期前",monthAgo:"1个月前",monthsAgo:"{delta}个月前",yearAgo:"1年前",yearsAgo:"{delta}年前",lessThanMinuteUntil:"從現在開始不到1分鐘",minuteUntil:"從現在開始約1分鐘",minutesUntil:"從現在開始約{delta}分鐘",hourUntil:"從現在開始1小時",hoursUntil:"從現在開始約{delta}小時",dayUntil:"從現在開始1天",daysUntil:"從現在開始{delta}天",weekUntil:"從現在開始1星期",weeksUntil:"從現在開始{delta}星期",monthUntil:"從現在開始一個月",monthsUntil:"從現在開始{delta}個月",yearUntil:"從現在開始1年",yearsUntil:"從現在開始{delta}年"}),"undefined"==typeof JSON&&(this.JSON={}),function(){var special={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"},escape=function(t){return special[t]||"\\u"+("0000"+t.charCodeAt(0).toString(16)).slice(-4)};JSON.validate=function(t){return t=t.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,"@").replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,"]").replace(/(?:^|:|,)(?:\s*\[)+/g,""),/^[\],:{}\s]*$/.test(t)},JSON.encode=JSON.stringify?function(t){return JSON.stringify(t)}:function(t){switch(t&&t.toJSON&&(t=t.toJSON()),typeOf(t)){case"string":return'"'+t.replace(/[\x00-\x1f\\"]/g,escape)+'"';case"array":return"["+t.map(JSON.encode).clean()+"]";case"object":case"hash":var e=[];return Object.each(t,(function(t,n){var r=JSON.encode(t);r&&e.push(JSON.encode(n)+":"+r)})),"{"+e+"}";case"number":case"boolean":return""+t;case"null":return"null"}return null},JSON.decode=function(string,secure){if(!string||"string"!=typeOf(string))return null;if(secure||JSON.secure){if(JSON.parse)return JSON.parse(string);if(!JSON.validate(string))throw new Error("JSON could not decode the input; security is enabled and the value is not secure.")}return eval("("+string+")")}}();
//공용 부분---------------------------------------------------------
bind = this || {};
function toJsJson(o){
    var type = typeOf(o);
    switch (type){
        case "array":
            o.each(function(item, idx){
                o[idx] = toJsJson(item);
            });
            return o;
        case "object":
            if (o.getClass){
                return JSON.parse(Java.type("com.x.base.core.project.gson.XGsonBuilder").toJson(o));
            }else{
                for (key in o){
                    o[key] = toJsJson(o[key]);
                }
            }
            return o;
        default:
            return o;
    }
}
var library = {
    'version': '4.0',
    "defineProperties": Object.defineProperties || function (obj, properties) {
        function convertToDescriptor(desc) {
            function hasProperty(obj, prop) {
                return Object.prototype.hasOwnProperty.call(obj, prop);
            }

            function isCallable(v) {
                // NB: modify as necessary if other values than functions are callable.
                return typeof v === "function";
            }

            if (typeof desc !== "object" || desc === null)
                throw new TypeError("bad desc");

            var d = {};

            if (hasProperty(desc, "enumerable"))
                d.enumerable = !!obj.enumerable;
            if (hasProperty(desc, "configurable"))
                d.configurable = !!obj.configurable;
            if (hasProperty(desc, "value"))
                d.value = obj.value;
            if (hasProperty(desc, "writable"))
                d.writable = !!desc.writable;
            if (hasProperty(desc, "get")) {
                var g = desc.get;

                if (!isCallable(g) && typeof g !== "undefined")
                    throw new TypeError("bad get");
                d.get = g;
            }
            if (hasProperty(desc, "set")) {
                var s = desc.set;
                if (!isCallable(s) && typeof s !== "undefined")
                    throw new TypeError("bad set");
                d.set = s;
            }

            if (("get" in d || "set" in d) && ("value" in d || "writable" in d))
                throw new TypeError("identity-confused descriptor");

            return d;
        }

        if (typeof obj !== "object" || obj === null)
            throw new TypeError("bad obj");

        properties = Object(properties);

        var keys = Object.keys(properties);
        var descs = [];

        for (var i = 0; i < keys.length; i++)
            descs.push([keys[i], convertToDescriptor(properties[keys[i]])]);

        for (var i = 0; i < descs.length; i++)
            Object.defineProperty(obj, descs[i][0], descs[i][1]);

        return obj;
    },
    'typeOf': function(item){
        if (item == null) return 'null';
        if (item.$family != null) return item.$family();
        if (item.constructor == Array) return 'array';

        if (item.nodeName){
            if (item.nodeType == 1) return 'element';
            if (item.nodeType == 3) return (/\S/).test(item.nodeValue) ? 'textnode' : 'whitespace';
        } else if (typeof item.length == 'number'){
            if (item.callee) return 'arguments';
            //if ('item' in item) return 'collection';
        }

        return typeof item;
    },
    'JSONDecode': JSON.parse,
    'JSONEncode': JSON.stringify
};
(function(){
    var o={"indexOf": {
            "value": function(item, from){
                var length = this.length >>> 0;
                for (var i = (from < 0) ? Math.max(0, length + from) : from || 0; i < length; i++){
                    if (this[i] === item) return i;
                }
                return -1;
            }
        }};
    Object.defineProperties(Array.prototype, o);
})();
bind.library = library;

if (!bind.oPrint) bind.oPrint = print;
var print = function(str, type){
    var d = new Date();
    var t = (type || "PRINT").toUpperCase();
    var l = "[script]";
    bind.oPrint(d.format("db")+"."+d.getMilliseconds()+" "+l+" "+t+" "+str);
}
var _parsePrint = function(str){
    if (!str && str!==0 && str!==false) return str;

    var text = (typeOf(str)!=="string") ? str.toString() : str;
    try{
        var i = 1;
        while (text.indexOf("%s")!==-1 && i<arguments.length){
            text = text.replace(/\%s/, arguments[i].toString());
            i++;
        }
        while (i<arguments.length){
            text += " "+arguments[i].toString();
            i++;
        }
    }catch(e){}
    return text;
};

var console = {
    log: function(){ print(_parsePrint.apply(this, arguments)); },
    error: function(){ print(_parsePrint.apply(this, arguments), "ERROR"); },
    info: function(){ print(_parsePrint.apply(this, arguments), "INFO"); },
    warn: function(){ print(_parsePrint.apply(this, arguments), "WARN"); }
}
var Error = function(msg){
    this.msg = msg;
}
Error.prototype.toString = function(){
    return this.msg;
}
var exec = function(code, _self){
    var returnValue;
    //try{
    if (!_self) _self = this;
    try {
        var f = eval("(function(){return function(){\n"+code+"\n}})();");
        returnValue = f.apply(_self);
    }catch(e){
        console.log("exec", new Error("exec script error"));
        console.log(e);
    }
    return returnValue;
}
bind.print = print;
bind.exec = exec;


//메서드 정의
bind.define = function(name, fun, overwrite){
    var over = true;
    if (overwrite===false) over = false;
    var o = {};
    o[name] = {"value": fun, "configurable": over};
    Object.defineProperties(bind, o);
}

//restful 서비스 Action
bind.Action = function(root, json){
    this.actions = json;
    // Object.keys(json).forEach(function(key){
    //     this.actions[key] = json[key];
    // });
    //Object.merge(actions[root], json);
    this.root = root;
    //this.actions = actions[root];

    var invokeFunction = function(service, parameters, key){
        var _self = this;
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
                    data = (n>++i) ? functionArguments[i] : null;
                }
            }else{
                parameters.each(function(p, x){
                    parameter[p] = (n>x) ? functionArguments[x] : null;
                });
                if (service.method && (service.method.toLowerCase()==="post" || service.method.toLowerCase()==="put")){
                    data = (n>++i) ? functionArguments[i] : null;
                }
                success = (n>++i) ? functionArguments[i] : null;
                failure = (n>++i) ? functionArguments[i] : null;
            }
            return _self.invoke({"name": key, "data": data, "parameter": parameter, "success": success, "failure": failure});
        };
    };
    var createMethod = function(service, key){
        var jaxrsUri = service.uri;
        var re = new RegExp("\{.+?\}", "g");
        var replaceWords = jaxrsUri.match(re);
        var parameters = [];
        if (replaceWords) parameters = replaceWords.map(function(s){
            return s.substring(1,s.length-1);
        });

        this[key] = invokeFunction.call(this, service, parameters, key);
    };
    Object.keys(this.actions).forEach(function(key){
        var service = this.actions[key];
        if (service.uri) if (!this[key]) createMethod.call(this, service, key);
    }, this);

    this.invoke = function(option){
        if (this.actions[option.name]){
            var uri = this.actions[option.name].uri;
            var method = this.actions[option.name].method || "get";
            if (option.parameter){
                Object.keys(option.parameter).forEach(function(key){
                    var v = option.parameter[key];
                    uri = uri.replace("{"+key+"}", v);
                });
            }
            var res = null;
            try{
                switch (method.toLowerCase()){
                    case "get":
                        res = bind.applications.getQuery(this.root, uri);
                        break;
                    case "post":
                        res = bind.applications.postQuery(this.root, uri, JSON.stringify(option.data));
                        break;
                    case "put":
                        res = bind.applications.putQuery(this.root, uri, JSON.stringify(option.data));
                        break;
                    case "delete":
                        res = bind.applications.deleteQuery(this.root, uri);
                        break;
                    default:
                        res = bind.applications.getQuery(this.root, uri);
                }
                if (res && res.getType().toString()==="success"){
                    var json = JSON.parse(res.toString());
                    if (option.success) option.success(json);
                }else{
                    if (option.failure) option.failure(((res) ? JSON.parse(res.toString()) : null));
                }
                return res;
            }catch(e){
                if (option.failure) option.failure(e);
            }
        }
    };
}
bind.Action.applications = bind.applications;

bind.Actions = {
    "loadedActions": {},
    "load": function(root){
        if (this.loadedActions[root]) return this.loadedActions[root];
        var jaxrsString = bind.applications.describeApi(root);
        var json = JSON.parse(jaxrsString.toString());
        if (json && json.jaxrs){
            var actionObj = {};
            json.jaxrs.each(function(o){
                if (o.methods && o.methods.length){
                    var actions = {};
                    o.methods.each(function(m){

                        var o = {"uri": "/"+m.uri};
                        if (m.method) o.method = m.method;
                        if (m.enctype) o.enctype = m.enctype;
                        actions[m.name] = o;
                    }.bind(this));
                    actionObj[o.name] = new bind.Action(root, actions);
                }
            }.bind(this));
            this.loadedActions[root] = actionObj;
            return actionObj;
        }
        return null;
    }
};

//조직 관련
var getNameFlag = function(name){
    var t = library.typeOf(name);
    if (t==="array"){
        var v = [];
        name.forEach(function(id){
            v.push((library.typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id);
        });
        return v;
    }else{
        return [(t==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name];
    }
};

bind.org = {
    "group": function() { return this.oGroup},
    "identity": function() { return this.oIdentity},
    "person": function() { return this.oPerson},
    "personAttribute": function() { return this.oPersonAttribute},
    "role": function() { return this.oRole},
    "unit": function() { return this.oUnit},
    "unitAttribute": function() { return this.oUnitAttribute},
    "unitDuty": function() { return this.oUnitDuty},
    "getObject": function(o, v){
        var arr = [];
        if (!v || !v.length){
            return null;
        }else{
            for (var i=0; i<v.length; i++){
                var g = o.getObject(v[i]);
                if (g) arr.push(JSON.parse(g.toString()));
            }
        }
        return arr;
    },

    //그룹***************
    //그룹 조회--그룹 객체 배열 반환
    getGroup: function(name){
        var v = this.oGroup.listObject(getNameFlag(name));
        var v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
    },

    //하위 그룹 조회--그룹 객체 배열 반환
    //nested  불리언  true 중첩 하위; false 직접 하위; 기본값 false;
    listSubGroup: function(name, nested){
        var v = null;
        if (nested){
            v = this.oGroup.listWithGroupSubNested(getNameFlag(name));
        }else{
            v = this.oGroup.listWithGroupSubDirect(getNameFlag(name));
        }
        return this.getObject(this.oGroup, v);
    },
    //상위 그룹 조회--그룹 객체 배열 반환
    //nested  불리언  true 중첩 상위; false 직접 상위; 기본값 false;
    listSupGroup:function(name, nested){
        var v = null;
        if (nested){
            v = this.oGroup.listWithGroupSupNested(getNameFlag(name));
        }else{
            v = this.oGroup.listWithGroupSupDirect(getNameFlag(name));
        }
        return this.getObject(this.oGroup, v);
    },

    //사용자 소속 그룹(중첩)--그룹 객체 배열 반환
    listGroupWithPerson:function(name){
        var v = this.oGroup.listWithPerson(getNameFlag(name));
        return this.getObject(this.oGroup, v);
    },

    //그룹의 역할 보유 여부--true, false 반환
    groupHasRole: function(name, role){
        nameFlag = (library.typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
        return this.oGroup.hasRole(nameFlag, getNameFlag(role));
    },

    //역할***************
    //역할 조회--역할 객체 배열 반환
    getRole: function(name){
        var v = this.oRole.listObject(getNameFlag(name));
        var v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
    },

    //사용자의 모든 역할(중첩)--역할 객체 배열 반환
    listRoleWithPerson:function(name){
        var v = this.oRole.listWithPerson(getNameFlag(name));
        return this.getObject(this.oRole, v);
    },

    //사용자***************
    //사용자의 역할 보유 여부--true, false 반환
    personHasRole: function(name, role){
        nameFlag = (library.typeOf(name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
        return this.oPerson.hasRole(nameFlag, getNameFlag(role));
    },

    //사용자 조회, 신분, 신분 소속 조직, 개인 소속 그룹, 개인 보유 역할 포함.
    getPersonData: function(name){
        var v = this.oPerson.getExt(name);
        var v_json = (!v) ? null: JSON.parse(v.toString());
        return v_json;
    },

    //사용자 조회--사용자 객체 배열 반환
    getPerson: function(name, findCN){
        var v = this.oPerson.listObject(getNameFlag(name), !!findCN);
        var v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        // if (!v || !v.length) v = null;
        // return (v && v.length===1) ? v[0] : v;
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
    },

    //하위 사용자 조회--사용자 객체 배열 반환
    //nested  불리언  true 중첩 하위; false 직접 하위; 기본값 false;
    listSubPerson: function(name, nested){
        var v = null;
        if (nested){
            v = this.oPerson.listWithPersonSubNested(getNameFlag(name));
        }else{
            v = this.oPerson.listWithPersonSubDirect(getNameFlag(name));
        }
        return this.getObject(this.oPerson, v);
    },

    //상위 사용자 조회--사용자 객체 배열 반환
    //nested  불리언  true 중첩 상위; false 직접 상위; 기본값 false;
    listSupPerson: function(name, nested){
        var v = null;
        if (nested){
            v = this.oPerson.listWithPersonSupNested(getNameFlag(name));
        }else{
            v = this.oPerson.listWithPersonSupDirect(getNameFlag(name));
        }
        return this.getObject(this.oPerson, v);
    },

    //그룹의 모든 사용자 조회--사용자 객체 배열 반환
    listPersonWithGroup: function(name){
        var v = this.oPerson.listWithGroup(getNameFlag(name));
        return this.getObject(this.oPerson, v);
        // if (!v || !v.length) v = null;
        // return v;
        // var v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        // return v_json;
    },

    //역할의 모든 사용자 조회--사용자 객체 배열 반환
    listPersonWithRole: function(name){
        var v = this.oPerson.listWithRole(getNameFlag(name));
        return this.getObject(this.oPerson, v);
    },

    //신분의 모든 사용자 조회--사용자 객체 배열 반환
    listPersonWithIdentity: function(name){
        var v = this.oPerson.listWithIdentity(getNameFlag(name));
        return this.getObject(this.oPerson, v);
    },

    //신분의 모든 사용자 조회--사용자 객체 배열 반환
    getPersonWithIdentity: function(name){
        var v = this.oPerson.listWithIdentity(getNameFlag(name));
        var arr = this.getObject(this.oPerson, v);
        return (arr && arr.length==1) ? arr[0] : arr;
    },

    //조직 구성원 사용자 조회--사용자 객체 배열 반환
    //nested  불리언  true 중첩된 모든 구성원; false 직접 구성원; 기본값 false;
    listPersonWithUnit: function(name, nested){
        var v = null;
        if (nested){
            v = this.oPerson.listWithUnitSubNested(getNameFlag(name));
        }else{
            v = this.oPerson.listWithUnitSubDirect(getNameFlag(name));
        }
        return this.getObject(this.oPerson, v);
    },

    //사용자 속성************
    //사용자 속성 값 추가(속성에 values 값 추가, 해당 속성이 없으면 생성)
    appendPersonAttribute: function(person, attr, values){
        var personFlag = (library.typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        return this.oPersonAttribute.appendWithPersonWithName(personFlag, attr, values);
    },

    //사용자 속성 값 설정(속성 값을 values로 변경, 해당 속성이 없으면 생성)
    setPersonAttribute: function(person, attr, values){
        var personFlag = (library.typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        return this.oPersonAttribute.setWithPersonWithName(personFlag, attr, values);
    },

    //사용자 속성 값 조회
    getPersonAttribute: function(person, attr){
        var personFlag = (library.typeOf(person)==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        var v = this.oPersonAttribute.listAttributeWithPersonWithName(personFlag, attr);
        var v_json = [];
        if (v && v.length){
            for (var i=0; i<v.length; i++){
                v_json.push(v[i].toString());
            }
        }
        return v_json;
    },

    //사용자의 모든 속성 이름 목록
    listPersonAttributeName: function(name){
        var p = getNameFlag(name);
        var nameList = [];
        for (var i=0; i<p.length; i++){
            var v = this.oPersonAttribute.listNameWithPerson(p[i]);
            if (v && v.length){
                for (var j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])==-1) nameList.push(v[j].toString());
                }
            }
        }
        return nameList;
    },

    // //사용자의 모든 속성 목록
    // listPersonAllAttribute: function(name){
    //     var p = getNameFlag(name);
    //     var v = this.oPersonAttribute.listNameWithPerson(p);
    //
    //     var data = {"personList":getNameFlag(name)};
    //     var v = null;
    //     orgActions.listPersonAllAttribute(data, function(json){v = json.data;}, null, false);
    //     return v;
    // },

    //신분**********
    //신분 조회
    getIdentity: function(name){
        var v = this.oIdentity.listObject(getNameFlag(name));
        var v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
        // if (!v || !v.length) v = null;
        // return (v && v.length===1) ? v[0] : v;
    },

    //사용자의 신분 목록
    listIdentityWithPerson: function(name){
        var v = this.oIdentity.listWithPerson(getNameFlag(name));
        return this.getObject(this.oIdentity, v);
    },

    //조직 구성원 신분 조회--신분 객체 배열 반환
    //nested  불리언  true 중첩된 모든 구성원; false 직접 구성원; 기본값 false;
    listIdentityWithUnit: function(name, nested){
        var v = null;
        if (nested){
            v = this.oIdentity.listWithUnitSubNested(getNameFlag(name));
        }else{
            v = this.oIdentity.listWithUnitSubDirect(getNameFlag(name));
        }
        return this.getObject(this.oIdentity, v);
    },

    //조직**********
    //조직 조회
    getUnit: function(name, findCN){
        var v = this.oUnit.listObject(getNameFlag(name), !!findCN);
        var v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
        // if (!v || !v.length) v = null;
        // return (v && v.length===1) ? v[0] : v;
    },

    //조직의 하위 조회--조직 객체 배열 반환
    //nested  불리언  true 중첩 하위; false 직접 하위; 기본값 false;
    listSubUnit: function(name, nested){
        var v = null;
        if (nested){
            v = this.oUnit.listWithUnitSubNested(getNameFlag(name));
        }else{
            v = this.oUnit.listWithUnitSubDirect(getNameFlag(name));
        }
        return this.getObject(this.oUnit, v);
    },

    //조직의 상위 조회--조직 객체 배열 반환
    //nested  불리언  true 중첩 상위; false 직접 상위; 기본값 false;
    listSupUnit: function(name, nested){
        var v = null;
        if (nested){
            v = this.oUnit.listWithUnitSupNested(getNameFlag(name));
        }else{
            v = this.oUnit.listWithUnitSupDirect(getNameFlag(name));
        }
        return this.getObject(this.oUnit, v);
    },

    //개인 신분으로 조직 조회
    //flag 숫자    몇 번째 단계의 조직을 조회할지 나타냄
    //     문자열  지정 유형의 조직 조회를 나타냄
    //     비어 있음     직접 소속 조직 조회를 나타냄
    getUnitByIdentity: function(name, flag){
        //getOrgActions();
        var getUnitMethod = "current";
        var v;
        if (flag){
            if (library.typeOf(flag)==="string") getUnitMethod = "type";
            if (library.typeOf(flag)==="number") getUnitMethod = "level";
        }
        var n = getNameFlag(name)[0];
        switch (getUnitMethod){
            case "current":
                v = this.oUnit.getWithIdentity(n);
                break;
            case "type":
                v = this.oUnit.getWithIdentityWithType(n, flag);
                break;
            case "level":
                v = this.oUnit.getWithIdentityWithLevel(n, flag);
                break;
        }
        var o = this.getObject(this.oUnit, [v]);
        return (o && o.length===1) ? o[0] : o;
    },

    //신분 소속 조직의 모든 상위 조직 목록
    listAllSupUnitWithIdentity: function(name){
        var v = this.oUnit.listWithIdentitySupNested(getNameFlag(name));
        return this.getObject(this.oUnit, v);
    },

    //사용자가 속한 모든 조직 조회(직속 조직)
    listUnitWithPerson: function(name){
        var v = this.oUnit.listWithPerson(getNameFlag(name));
        return this.getObject(this.oUnit, v);
    },

    //사용자 소속 조직의 모든 상위 조직 목록
    listAllSupUnitWithPerson: function(name){
        var v = this.oUnit.listWithPersonSupNested(getNameFlag(name));
        return this.getObject(this.oUnit, v);
    },

    //조직 속성으로 일치하는 모든 조직 조회
    listUnitWithAttribute: function(name, attribute){
        var v = this.oUnit.listWithUnitAttribute(name, attribute);
        return this.getObject(this.oUnit, v);
    },

    //조직 직무로 일치하는 모든 조직 조회
    listUnitWithDuty: function(name, id){
        var idflag = (library.typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id;
        var v = this.oUnit.listWithUnitDuty(name, idflag);
        return this.getObject(this.oUnit, v);
    },

    //조직 직무***********
    //지정 조직 직무의 신분 조회
    getDuty: function(duty, id){
        var unit = (library.typeOf(id)==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id;
        var v = this.oUnitDuty.listIdentityWithUnitWithName(unit, duty);
        return this.getObject(this.oIdentity, v);
    },

    //신분의 모든 직무 이름 조회
    listDutyNameWithIdentity: function(name){
        var ids = getNameFlag(name);
        var nameList = [];
        for (var i=0; i<ids.length; i++){
            var v = this.oUnitDuty.listNameWithIdentity(ids[i]);
            if (v && v.length){
                for (var j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])==-1) nameList.push(v[j].toString());
                }
            }
        }
        return nameList;
    },

    //조직의 모든 직무 이름 조회
    listDutyNameWithUnit: function(name){
        var ids = getNameFlag(name);
        var nameList = [];
        for (var i=0; i<ids.length; i++){
            var v = this.oUnitDuty.listNameWithUnit(ids[i]);
            if (v && v.length){
                for (var j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])==-1) nameList.push(v[j].toString());
                }
            }
        }
        return nameList;
    },

    //조직의 모든 직무 조회
    listUnitAllDuty: function(name){
        var u = getNameFlag(name)[0];
        var ds = this.oUnitDuty.listNameWithUnit(u);
        var o = []
        for (var i=0; i<ds.length; i++){
            v = this.oUnitDuty.listIdentityWithUnitWithName(u, ds[i]);
            o.push({"name": ds[i], "identityList": this.getObject(this.oIdentity, v)});
        }
        return o;
    },

    //조직 속성**************
    //조직 속성 값 추가(속성에 values 값 추가, 해당 속성이 없으면 생성)
    appendUnitAttribute: function(unit, attr, values){
        var unitFlag = (library.typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        return this.oUnitAttribute.appendWithUnitWithName(unitFlag, attr, values);
    },

    //조직 속성 값 설정(속성 값을 values로 변경, 해당 속성이 없으면 생성)
    setUnitAttribute: function(unit, attr, values){
        var unitFlag = (library.typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        return this.oUnitAttribute.setWithUnitWithName(unitFlag, attr, values);
    },

    //조직 속성 값 조회
    getUnitAttribute: function(unit, attr){
        var unitFlag = (library.typeOf(unit)==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        var v = this.oUnitAttribute.listAttributeWithUnitWithName(unitFlag, attr);
        var v_json = [];
        if (v && v.length){
            for (var i=0; i<v.length; i++){
                v_json.push(v[i].toString());
            }
        }
        return v_json;
    },

    //조직의 모든 속성 이름 목록
    listUnitAttributeName: function(name){
        var p = getNameFlag(name);
        var nameList = [];
        for (var i=0; i<p.length; i++){
            var v = this.oUnitAttribute.listNameWithUnit(p[i]);
            if (v && v.length){
                for (var j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])==-1) nameList.push(v[j]);
                }
            }
        }
        return nameList;
    },

    //조직의 모든 속성 목록
    listUnitAllAttribute: function(name){
        var u = getNameFlag(name)[0];
        var ds = this.oUnitAttribute.listNameWithUnit(u);
        var o = []
        for (var i=0; i<ds.length; i++){
            v = this.getUnitAttribute(u, ds[i]);
            o.push({"name": ds[i], "valueList":v});
        }
        return o;
    }
};
Object.defineProperties(bind.org, {
    "oGroup": { "configurable": true, "get": function(){return bind.java_resources.getOrganization().group()} },
    "oIdentity": { "configurable": true, "get": function(){return bind.java_resources.getOrganization().identity()} },
    "oPerson": { "configurable": true, "get": function(){return bind.java_resources.getOrganization().person()} },
    "oPersonAttribute": { "configurable": true, "get": function(){return bind.java_resources.getOrganization().personAttribute()} },
    "oRole": { "configurable": true, "get": function(){return bind.java_resources.getOrganization().role()} },
    "oUnit": { "configurable": true, "get": function(){return bind.java_resources.getOrganization().unit()} },
    "oUnitAttribute": { "configurable": true, "get": function(){return bind.java_resources.getOrganization().unitAttribute()} },
    "oUnitDuty": { "configurable": true, "get": function(){return bind.java_resources.getOrganization().unitDuty()} }
});

//필요한 서비스 정의
bind.processActions = new bind.Action("x_processplatform_assemble_surface", {
    "getDictionary": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{applicationFlag}"},
    "getDictRoot": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/data"},
    "getDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data"},
    "setDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data", "method": "PUT"},
    "addDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data", "method": "POST"},
    "deleteDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data", "method": "DELETE"},
    "getScript": {"uri": "/jaxrs/script/{flag}/application/{applicationFlag}", "method": "POST"},
});
bind.cmsActions = new bind.Action("x_cms_assemble_control", {
    "getDictionary": {"uri": "/jaxrs/design/appdict/{appDictId}"},
    "getDictRoot": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/data"},
    "getDictData": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data"},
    "setDictData": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data", "method": "PUT"},
    "addDictData": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data", "method": "POST"},
    "deleteDictData": {"uri": "/jaxrs/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data", "method": "DELETE"},
    "getDictRootAnonymous" : {"uri": "/jaxrs/anonymous/surface/appdict/{appDictId}/appInfo/{appId}/data"},
    "getDictDataAnonymous" : {"uri": "/jaxrs/anonymous/surface/appdict/{appDictId}/appInfo/{appId}/{path}/data"},
    "getScript": {"uri": "/jaxrs/script/{flag}/appInfo/{appInfoFlag}", "method": "POST"},
});
bind.portalActions = new bind.Action("x_portal_assemble_surface", {
    "getDictionary": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}"},
    "getDictRoot": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}/data"},
    "getDictData": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}/{path}/data"},
    "setDictData": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}/{path}/data", "method": "PUT"},
    "addDictData": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}/{path}/data", "method": "POST"},
    "deleteDictData": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}/{path}/data", "method": "DELETE"},
    "getScript":  {"uri": "/jaxrs/script/portal/{portal}/name/{name}","method": "POST"}
});
bind.serviceActions = new bind.Action("x_program_center", {
    "getDictionary": {"uri": "/jaxrs/dict/{id}"},
    "getDictRoot": {"uri": "/jaxrs/dict/{dictFlag}/data"},
    "getDictData": {"uri": "/jaxrs/dict/{dictFlag}/{path}/data"},
    "setDictData": {"uri": "/jaxrs/dict/{dictFlag}/{path}/data", "method": "PUT"},
    "addDictData": {"uri": "/jaxrs/dict/{dictFlag}/{path}/data", "method": "POST"},
    "deleteDictData": {"uri": "/jaxrs/dict/{dictFlag}/{path}/data", "method": "DELETE"},
    "getScript":  {"uri": "/jaxrs/script/name/{name}","method": "POST"}
});

//include 스크립트 참조
//optionsOrName : {
//  type : "", 기본값 process, portal  process  cms 가능
//  application : "", 포털/프로세스/CMS의 이름/별칭/id, 기본값은 현재 앱
//  name : "" // 스크립트 이름/별칭/id
//}
//또는 name: "" // 스크립트 이름/별칭/id
var includedScripts = bind.includedScripts || {};
bind.includedScripts = includedScripts;
bind.include = function( optionsOrName , callback ){
    var options = optionsOrName;
    if( typeOf( options ) == "string" ){
        options = { name : options };
    }
    var name = options.name;
    var type;
    if( options.type === "service" ){
        type = options.type;
    }else{
        type  = ( options.type && options.application ) ?  options.type : "process";
    }
    var application = type === "service" ? "service" : options.application;

    if (!name || !type || !application){
        console.log("include", new Error("can not find script. missing script name or application"));
        return false;
    }

    if (!includedScripts[application]) includedScripts[application] = [];

    if (includedScripts[application].indexOf( name )> -1){
        if (callback) callback.apply(this);
        return;
    }

    var scriptAction;
    var scriptData;
    switch ( type ){
        case "portal" :
            bind.portalActions.getScript( application, name, {"importedList":includedScripts[application]}, function(json){
                if (json.data){
                    includedScripts[application] = includedScripts[application].concat(json.data.importedList);
                    scriptData = json.data;
                }
            }.bind(this));
            break;
        case "process" :
            bind.processActions.getScript( name, application, {"importedList":includedScripts[application]}, function(json){
                if (json.data){
                    includedScripts[application] = includedScripts[application].concat(json.data.importedList);
                    scriptData = json.data;
                }
            }.bind(this));
            break;
        case "cms" :
            bind.cmsActions.getScript(name, application, {"importedList":includedScripts[application]}, function(json){
                if (json.data){
                    includedScripts[application] = includedScripts[application].concat(json.data.importedList);
                    scriptData = json.data;
                }
            }.bind(this));
            break;
        case "service" :
            bind.serviceActions.getScript(name, {"importedList":includedScripts[application]}, function(json){
                if (json.data){
                    includedScripts[application] = includedScripts[application].concat(json.data.importedList);
                    scriptData = json.data;
                }
            }.bind(this));
            break;
    }
    includedScripts[application].push(name);
    if (scriptData && scriptData.text){
        exec(scriptData.text, this);
        if (callback) callback.apply(this);
    }
};

//optionsOrName : {
//  type : "", //기본값 process, process  cms 가능
//  application : "", //프로세스/CMS의 이름/별칭/id, 기본값은 현재 앱
//  name : "", // 데이터 사전 이름/별칭/id
//  enableAnonymous : false //로그인하지 않은 상태에서 CMS 데이터 사전 읽기 허용
//}
//또는 name: "" // 데이터 사전 이름/별칭/id

bind.Dict = function(optionsOrName){
    var options = optionsOrName;
    if( typeOf( options ) == "string" ){
        options = { name : options };
    }
    var name = this.name = options.name;
    var type;
    if( options.type === "service"){
        type = options.type;
    }else{
        type = ( options.type && options.application ) ?  options.type : "process";
    }
    var applicationId = options.application || ((bind.java_workContext) ? bind.java_workContext.getWork().application : "");
    var enableAnonymous = options.enableAnonymous || false;

    //MWF.require("MWF.xScript.Actions.DictActions", null, false);
    var action;
    if( type == "cms" ){
        action = bind.cmsActions;
    }else if( type == "service" ){
        action = bind.serviceActions;
    }else if( type == "portal" ){
        action = bind.portalActions;
    }else{
        action = bind.processActions;
    }

    var encodePath = function( path ){
        var arr = path.split(/\./g);
        var ar = arr.map(function(v){
            return encodeURIComponent(v);
        });
        return ( type === "portal" || type === "service" ) ? ar.join(".") : ar.join("/");
    };
    this.get = function(path, success, failure){
        var value = null;
        if( type === "service" ){
            if (path){
                var p = encodePath( path );
                action.getDictData(encodeURIComponent(this.name), p, function(json){
                    value = json.data;
                    if (success) success(json.data);
                }, function(xhr, text, error){
                    if (failure) failure(xhr, text, error);
                });
            }else{
                action.getDictRoot(encodeURIComponent(this.name), function(json){
                    value = json.data;
                    if (success) success(json.data);
                }, function(xhr, text, error){
                    if (failure) failure(xhr, text, error);
                }, false);
            }
        }else{
            if (path){
                var p = encodePath( path );
                action[(enableAnonymous && type == "cms") ? "getDictDataAnonymous" : "getDictData"](encodeURIComponent(this.name), applicationId, p, function(json){
                    value = json.data;
                    if (success) success(json.data);
                }, function(xhr, text, error){
                    if (failure) failure(xhr, text, error);
                });
            }else{
                action[(enableAnonymous && type == "cms") ? "getDictRootAnonymous" : "getDictRoot"](encodeURIComponent(this.name), applicationId, function(json){
                    value = json.data;
                    if (success) success(json.data);
                }, function(xhr, text, error){
                    if (failure) failure(xhr, text, error);
                }, false);
            }
        }

        return value;
    };
    this.set = function(path, value, success, failure){
        var p = encodePath( path );
        //var p = path.replace(/\./g, "/");
        if( type === "service" ){
            action.setDictData(encodeURIComponent(this.name), p, value, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false, false);
        }else{
            action.setDictData(encodeURIComponent(this.name), applicationId, p, value, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false, false);
        }
    };
    this.add = function(path, value, success, failure){
        var p = encodePath( path );
        //var p = path.replace(/\./g, "/");
        if( type === "service" ) {
            action.addDictData(encodeURIComponent(this.name), p, value, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false, false);
        }else{
            action.addDictData(encodeURIComponent(this.name), applicationId, p, value, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false, false);
        }
    };
    this["delete"] = function(path, success, failure){
        var p = encodePath( path );
        //var p = path.replace(/\./g, "/");
        if( type === "service" ) {
            action.deleteDictData(encodeURIComponent(this.name), p, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false, false);
        }else{
            action.deleteDictData(encodeURIComponent(this.name), applicationId, p, function(json){
                if (success) success(json.data);
            }, function(xhr, text, error){
                if (failure) failure(xhr, text, error);
            }, false, false);
        }
    };
    this.destory = this["delete"];
};

bind.Table = function(name){
    this.name = name;
    this.action = Actions.load("x_query_assemble_surface").TableAction;

    this.listRowNext = function(id, count, success, error, async){
        this.action.listRowNext(this.name, id, count, success, error, async);
    };

    this.listRowPrev = function(id, count, success, error, async){
        this.action.listRowPrev(this.name, id, count, success, error, async);
    };

    this.listRowSelect = function(where, orderBy, size, success, error, async){
        this.action.listRowSelect(this.name, {"where": where, "orderBy": orderBy, "size": size || ""}, success, error, async);
    };
    this.listRowSelectWhere = function(where, success, error, async){
        this.action.listRowSelectWhere(this.name, where, success, error, async);
    };

    this.rowCountWhere = function(where, success, error, async){
        this.action.rowCountWhere(this.name, where, success, error, async);
    };

    this.deleteRow = function(id, success, error, async){
        this.action.rowDelete(this.name, id, success, error, async);
    };

    this.deleteAllRow = function(success, error, async){
        this.action.rowDeleteAll(this.name, success, error, async);
    };

    this.getRow = function(id, success, error, async){
        this.action.rowGet(this.name, id, success, error, async);
    };

    this.insertRow = function(data, success, error, async){
        this.action.rowInsert(this.name, data, success, error, async);
    };

    this.addRow = function(data, success, error, async){
        this.action.rowInsertOne(this.name, data, success, error, async);
    };

    this.updateRow = function(id, data, success, error, async){
        this.action.rowUpdate(this.name, id, data, success, error, async);
    };

    this.partUpdateRow = function(id, data, success, error, async){
        this.action.rowPartUpdate(this.name, id, data, success, error, async);
    };
}

bind.statement = {
     execute: function (obj, callback) {
        if( obj.format ){
            return this._execute(obj, callback, obj.format);
        }else{
            if( this.needCheckFormat(obj) ){
                var value;
                var _self = this;
                bind.Actions.load("x_query_assemble_surface").StatementAction.getFormat(obj.name, function(json){
                    value = _self._execute(obj, callback, json.data.format);
                });
                return value;
            }else{
                return this._execute(obj, callback, "");
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
                if( ["dateTimeValue", "datetimeValue", "dateValue", "timeValue"].indexOf( fType ) > -1 )return true;
            }
        }
        return false;
    },
    _execute: function (statement, callback, format) {
        var parameter = this.parseParameter(statement.parameter, format);
        var filterList = this.parseFilter(statement.filter, parameter, format);
        var obj = {
            "filterList": filterList,
            "parameter" : parameter
        };
        var value;
        bind.Actions.load("x_query_assemble_surface").StatementAction.executeV2(
            statement.name, statement.mode || "data", statement.page || 1, statement.pageSize || 20, obj,
            function (json) {
                if (callback) callback(json);
                value = json;
            }
        );
        return value;
    },
    parseFilter : function( filter, parameter, format ){
        if( typeOf(filter) !== "array" )return [];
        if( !parameter )parameter = {};
        var filterList = [];
        ( filter || [] ).each( function (d) {
            //var parameterName = d.path.replace(/\./g, "_");
            var pName = d.path.replace(/\./g, "_");

            var parameterName = pName;
            var suffix = 1;
            while( parameter[parameterName] ){
                parameterName = pName + "_" + suffix;
                suffix++;
            }

            var value = d.value;
            if( d.comparison === "like" || d.comparison === "notLike" ){
                if( value.substr(0, 1) !== "%" )value = "%"+value;
                if( value.substr(value.length-1,1) !== "%" )value = value+"%";
                parameter[ parameterName ] = value; //"%"+value+"%";
            }else{
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
                parameter[ parameterName ] = value;
            }
            d.value = parameterName;

            filterList.push( d );
        });
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
    "select": function () {}
};

bind.view = {
    "lookup": function(view, callback){
        var filterList = {"filterList": (view.filter || null)};
        var value;
        bind.Actions.load("x_query_assemble_surface").ViewAction.executeWithQuery(view.view, view.application, filterList, function(json){
            var data = {
                "grid": json.data.grid || json.data.groupGrid,
                "groupGrid": json.data.groupGrid
            };
            if (callback) callback(data);
            value = data;
        });
        return value;
    },
    "select": function(view, callback, options){}
};

bind.service = {
    restful: function(method, url, headers, body, connectTimeout, readTimeout){
        var service = bind.java_resources.getWebservicesClient();
        var bodyData = ((typeof body)==="object") ? JSON.stringify(body) : (body||"");
        var res = service.restful(method, url, (headers||null), bodyData, (connectTimeout||2000), (readTimeout||300000));
        var o = {
            "responseCode" : res.responseCode,
            "headers" : res.headers,
            "body": res.body
        }
        try {
            o.json = JSON.parse(res.body);
        }catch(e){}
        return o;
    },

    "get": function(url, headers, connectTimeout, readTimeout){
        return this.restful("get", url, headers, "", connectTimeout, readTimeout);
    },

    "post": function(url, headers, body, connectTimeout, readTimeout){
        return this.restful("post", url, headers, body, connectTimeout, readTimeout);
    },

    soap: function(wsdl, method, pars){
        var service = bind.java_resources.getWebservicesClient();
        return service.soap(wsdl, method, pars);
    },
    soapXml: function(wsdl, xml){
        var service = bind.java_resources.getWebservicesClient();
        return service.jaxwsXml(wsdl, xml);
    }
}

//----------------------------------------------------------

//java_workcontext work 컨텍스트 객체, 프로세스 관련 스크립트에서 사용 가능
/**
 * 프로세스 이벤트, 프로세스 라우트 이벤트, 프로세스 활동 이벤트에서 workContext로 프로세스 관련 프로세스 인스턴스 객체 데이터를 가져올 수 있습니다.
 * @module server.workContext
 * @o2cn 프로세스 인스턴스
 * @o2category server.process
 * @o2range {Process} 모든 프로세스 설정의 스크립트에서 이 객체 사용 가능
 * @o2ordernumber 200
 * @o2syntax
 * //양식이나 프로세스의 각 내장 스크립트에서 this로 현재 프로세스 인스턴스 데이터를 조회할 수 있습니다. 예:
 * var context = this.workContext;
 */
bind.workContext = {
    /**
     * 현재 프로세스 인스턴스가 진행 중이고 현재 사용자에게 결재 대기가 있으면 현재 사용자의 결재 대기 객체를 반환합니다.
     * 실제로 백엔드 활동 이벤트에서는 대부분 이 메서드로 taskCompleted 객체를 가져옵니다. 인공 활동 이벤트에서 「결재 대기 처리 전」 이벤트를 제외한 다른 이벤트 처리 시점에는 현재 사용자의 결재 대기가 이미 처리 완료로 바뀌었기 때문입니다.
     * @summary 현재 프로세스에서 현재 사용자와 관련된 결재 대기 또는 처리 완료 객체(task 객체 또는 taskCompleted 객체) 조회.
     * @methodOf module:server.workContext
     * @o2ActionOut x_processplatform_assemble_surface.TaskAction.get|example=Task|Task 객체:
     * @method getTask
     * @static
     * @return {(Task|TaskCompleted|Null)} 현재 사용자의 결재 대기 작업 객체(task). 현재 사용자에게 이 프로세스 인스턴스의 결재 대기가 없거나 프로세스 인스턴스가 이미 종료되었으면 null 반환.
     * @o2syntax
     * var task = this.workContext.getTask();
     */
    "getTask": function(){  //work로 현재 처리 중인 task 또는 TaskCompleted json 문자열 조회. 진행 과정에서만 조회 가능
        var taskString = bind.java_workContext.getTaskOrTaskCompleted();
        var task = (taskString) ? JSON.parse(taskString) : null;
        if (task){
            task.personDn = task.person || "";
            task.unitDn = task.unit || "";
            task.unitDnList = task.unitList || "";
            task.identityDn = task.identity || "";
            task.creatorPersonDn = task.creatorPerson ||"";
            task.creatorUnitDn = task.creatorUnit ||"";
            task.creatorUnitDnList = task.creatorUnitList ||"";
            task.creatorIdentityDn = task.creatorIdentity ||"";
        }
        return task;
    },
    /**
     * 현재 프로세스 인스턴스 객체 조회: work 객체 또는 workCompleted 객체.
     * @method getWork
     * @methodOf module:server.workContext
     * @static
     * @return {(Work|WorkCompleted)} 프로세스 인스턴스 객체; 프로세스가 종료된 경우 종료된 프로세스 인스턴스 객체 반환.
     * @o2ActionOut x_processplatform_assemble_surface.WorkAction.manageGet|example=Work|ignoreNoDescr=true|ignoreProps=[properties,manualTaskIdentityMatrix]|Work 객체:
     * @o2ActionOut x_processplatform_assemble_surface.WorkCompletedAction.get|example=WorkCompleted|ignoreProps=[properties,data,taskCompletedList,readCompletedList,reviewList,recordList,workLogList,storeForm,mobileStoreForm]|WorkCompleted 객체:
     * @o2syntax
     * var work = this.workContext.getWork();
     */
    "getWork": function(){  //work 문자열
        var work = JSON.parse(bind.java_workContext.getWork());
        work.creatorPersonDn = work.creatorPerson ||"";
        work.creatorUnitDn = work.creatorUnit ||"";
        work.creatorUnitDnList = work.creatorUnitList ||"";
        work.creatorIdentityDn = work.creatorIdentity ||"";
        return work;
    },

    /**
     * 현재 프로세스 인스턴스의 모든 결재 대기 객체 조회. 프로세스 인스턴스가 이미 완료된 경우 빈 배열 반환.
     * @method getTaskList
     * @methodOf module:server.workContext
     * @o2ActionOut x_processplatform_assemble_surface.TaskAction.listWithWork|example=Task
     * @static
     * @return {(Task[])} 결재 대기 작업 목록.
     * @o2syntax
     * var taskList = this.workContext.getTaskList();
     */
    "getTaskList": function(){return JSON.parse(bind.java_workContext.getTaskList());},                       //work로 모든 task 배열 json 문자열 조회

    /**
     * 현재 프로세스 인스턴스의 모든 처리 완료 객체 조회. 프로세스 인스턴스를 처리한 사람이 없으면 빈 배열 반환.
     * @method getTaskCompletedList
     * @methodOf module:server.workContext
     * @static
     * @return {(TaskCompleted[])} 처리 완료 작업 목록.
     * @o2ActionOut x_processplatform_assemble_surface.TaskCompletedAction.listWithWork|example=Task
     * @o2syntax
     * var taskCompletedList = this.workContext.getTaskCompletedList();
     */
    "getTaskCompletedList": function(){return JSON.parse(bind.java_workContext.getTaskCompletedList());},     //work로 모든 TaskCompleted 배열 json 문자열 조회

    /**
     * @summary 현재 프로세스 인스턴스의 모든 열람 대기 객체 배열을 조회합니다. 열람 대기가 없으면 빈 배열을 반환합니다.
     * @method getReadList
     * @methodOf module:server.workContext
     * @static
     * @return {(Read[])} 현재 프로세스 인스턴스의 모든 열람 대기 객체 배열.
     * @o2ActionOut x_processplatform_assemble_surface.ReadAction.get|example=Read
     * @o2syntax
     * var readList = this.workContext.getReadList();
     */
    "getReadList": function(){return JSON.parse(bind.java_workContext.getReadList());},                       //work로 모든 read 배열 json 문자열 조회

    /**
     * @summary 현재 프로세스 인스턴스의 모든 열람 완료 객체를 조회합니다. 열람 완료가 없으면 빈 배열을 반환합니다.
     * @method getReadCompletedList
     * @methodOf module:server.workContext
     * @static
     * @return {(ReadCompleted[])} 현재 프로세스 인스턴스의 모든 열람 완료 객체 배열.
     * @o2ActionOut x_processplatform_assemble_surface.ReadCompletedAction.listWithWork|example=Read
     * @o2syntax
     * var readCompletedList = this.workContext.getReadCompletedList();
     */
    "getReadCompletedList": function(){return JSON.parse(bind.java_workContext.getReadCompletedList());},     //work로 모든 ReadCompleted 배열 json 문자열 조회

    /**
     * @summary 현재 프로세스 인스턴스의 모든 review 객체 조회. review가 없으면 빈 배열 반환.
     * @method getReviewList
     * @alias getReviewListByJob
     * @methodOf module:server.workContext
     * @static
     * @return {(Review[])} 현재 프로세스 인스턴스의 모든 review 객체 배열.
     * @o2ActionOut x_processplatform_assemble_surface.ReviewAction.listWithJob|example=Review
     * @o2syntax
     * var reviewList = this.workContext.getReviewList();
     */
    "getReviewList": function(){return JSON.parse(bind.java_workContext.getJobReviewList());},

    /**
     * @summary getTaskListByJob 메서드의 별칭.
     * @method getJobTaskList
     * @static
     * @see server.module:workContext.getTaskListByJob
     */
    "getJobTaskList": function(){return JSON.parse(bind.java_workContext.getJobTaskList());},                 //jobid로 모든 task 배열 json 문자열 조회

    /**
     * @summary getTaskCompletedListByJob 메서드의 별칭.
     * @method getJobTaskCompletedList
     * @static
     * @see server.module:workContext.getTaskCompletedListByJob
     */
    "getJobTaskCompletedList": function(){return JSON.parse(bind.java_workContext.getJobTaskCompletedList());},   //jobid로 모든 TaskCompleted 배열 json 문자열 조회
    /**
     * @summary getReadListByJob 메서드의 별칭.
     * @method getJobReadList
     * @static
     * @see server.module:workContext.getReadListByJob
     */
    "getJobReadList": function(){return JSON.parse(bind.java_workContext.getJobReadList());},                     //jobid로 모든 read 배열 json 문자열 조회
    /**
     * @summary getReadCompletedListByJob 메서드의 별칭.
     * @method getJobReadCompletedList
     * @static
     * @see server.module:workContext.getReadCompletedListByJob
     */
    "getJobReadCompletedList": function(){return JSON.parse(bind.java_workContext.getJobReadCompletedList());},   //jobid로 모든 ReadCompleted 배열 json 문자열 조회
    /**
     * @summary getReviewList 메서드의 별칭.
     * @method getJobReviewList
     * @static
     * @see server.module:workContext.getReviewList
     */
    "getJobReviewList": function (){return this.getReviewList();},                                                                        //jobid로 모든 Review 배열 json 문자열 조회

    /**
     * 현재 업무 문서의 job으로 현재 프로세스 인스턴스의 모든 결재 대기 객체 조회. 프로세스 인스턴스가 이미 완료된 경우 빈 배열 반환.
     * @method getTaskListByJob
     * @methodOf module:server.workContext
     * @o2ActionOut x_processplatform_assemble_surface.TaskAction.listWithJob|example=Task
     * @static
     * @return {(Task[])} 결재 대기 작업 목록.
     * @o2syntax
     * var taskList = this.workContext.getTaskListByJob();
     */
    "getTaskListByJob": function(){return this.getJobTaskList();},
    /**
     * 현재 업무 문서의 job으로 현재 프로세스 인스턴스의 모든 처리 완료 객체를 조회합니다. 처리한 사람이 없으면 빈 배열을 반환합니다.
     * @method getTaskCompletedListByJob
     * @methodOf module:server.workContext
     * @static
     * @return {(TaskCompleted[])} 처리 완료 작업 목록.
     * @o2ActionOut x_processplatform_assemble_surface.TaskCompletedAction.listWithJob|example=Task
     * @o2syntax
     * var taskCompletedList = this.workContext.getTaskCompletedListByJob();
     */
    "getTaskCompletedListByJob": this.getJobTaskCompletedList,
    /**
     * @summary 현재 업무 문서의 job으로 현재 프로세스 인스턴스의 모든 열람 대기 객체를 조회합니다. 열람 대기가 없으면 빈 배열을 반환합니다.
     * @method getReadListByJob
     * @methodOf module:server.workContext
     * @static
     * @return {(Read[])} 현재 프로세스 인스턴스의 모든 열람 대기 객체 배열.
     * @o2ActionOut x_processplatform_assemble_surface.ReadAction.listWithJob|example=Read
     * @o2syntax
     * var readList = this.workContext.getReadListByJob();
     */
    "getReadListByJob": function(){return this.getJobReadList();},
    /**
     * @summary 현재 업무 문서의 job으로 현재 프로세스 인스턴스의 모든 열람 완료 객체를 조회합니다. 열람 완료가 없으면 빈 배열을 반환합니다.
     * @method getReadCompletedListByJob
     * @methodOf module:server.workContext
     * @static
     * @return {(ReadCompleted[])} 현재 프로세스 인스턴스의 모든 열람 완료 객체 배열.
     * @o2ActionOut x_processplatform_assemble_surface.ReadCompletedAction.listWithJob|example=Read
     * @o2syntax
     * var readCompletedList = this.workContext.getReadCompletedListByJob();
     */
    "getReadCompletedListByJob": function(){return this.getJobReadCompletedList();},
    /**
     * @summary getReviewList 메서드의 별칭.
     * @method getReviewListByJob
     * @static
     * @see server.module:workContext.getReviewList
     */
    "getReviewListByJob": function(){return this.getJobReviewList();},

    /**
     * 현재 프로세스 인스턴스가 있는 활동 노드 객체 조회: activity 객체.
     * @method getActivity
     * @static
     * @return {(Activity|Null)} 현재 프로세스 인스턴스가 있는 활동 노드 객체, 프로세스 인스턴스가 이미 완료된 경우 null 반환.
     * <pre><code class='language-js'>{
     *      "id": "801087c5-a4e6-4b91-bf4d-a81cdaa04471", //노드 ID
     *      "name": "처리",  //노드 이름
     *      "description": "", //노드 설명
     *      "alias": "",  //노드 별칭
     *      "resetRange": "department", //처리자 재지정 범위
     *      "resetCount": 0,  //처리자 재지정 수
     *      "allowReset": true, //재지정 허용 여부
     *      "manualMode": "single", //처리 방식: 단일 single, 병렬 parallel, 순차 queue, 선점 grab
     *      "customData": { //노드의 사용자 정의 속성, 설정하지 않으면 출력하지 않음
     *
     *      }
     * }</code></pre>
     * @o2syntax
     * var activity = this.workContext.getActivity();
     */
    "getActivity": function(){return JSON.parse(bind.java_workContext.getActivity());},       //활동 객체 문자열

    /**
     * @summary 현재 프로세스 인스턴스의 모든 프로세스 기록(WorkLog) 조회.
     * @method getWorkLogList
     * @static
     * @return {WorkLog[]} 프로세스 기록 객체.
     * @o2ActionOut x_processplatform_assemble_surface.WorkLogAction.listWithJob|example=WorkLog|ignoreProps=[properties,goBackFromActivityType]
     * @o2syntax
     * var workLogList = this.workContext.getWorkLogList();
     */
    "getWorkLogList": function(){return JSON.parse(bind.java_workContext.getWorkLogList());}, //WorkLogList 객체 배열 문자열

    /**
     * @summary 현재 프로세스 인스턴스의 모든 프로세스 기록(Record) 조회.
     * @method getRecordList
     * @o2ActionOut x_processplatform_assemble_surface.RecordAction.listWithJob|example=Record
     * @static
     * @return {Record[]} 프로세스 기록(Record) 객체.
     * @o2syntax
     * var recordList = this.workContext.getRecordList();
     */
    "getRecordList": function(){return JSON.parse(bind.java_workContext.getRecordList());}, //RecordList 객체 배열 문자열, 추가 필요

    /**
     * @summary 마지막 Record 객체. 활동 진행 완료 이벤트에서는 이번 진행의 record를, 그 밖의 이벤트에서는 job 전체의 마지막 record를 가져오며 이번 진행의 record가 아님.
     * @method getRecord
     * @o2ActionOut x_processplatform_assemble_surface.RecordAction.listWithJob|example=Record
     * @static
     * @return {Record[]} 프로세스 기록(Record) 객체.
     * @o2syntax
     * var record = this.workContext.getRecord();
     */
    "getRecord": function(){return JSON.parse(bind.java_workContext.getRecord());}, //마지막 Record 객체(활동 진행 완료 이벤트에서는 이번 진행의 record를, 그 밖의 이벤트에서는 job 전체의 마지막 record를 가져오며 이번 진행의 record가 아님)

    /**
     * @summary 현재 프로세스 인스턴스의 첨부파일 객체 목록 조회.
     * @method getAttachmentList
     * @static
     * @return {WorkAttachmentData[]} 첨부파일 데이터.
     * @o2ActionOut x_processplatform_assemble_surface.AttachmentAction.getWithWorkOrWorkCompleted|example=Attachment|ignoreProps=[properties]
     * @o2syntax
     * //첨부파일 목록 조회
     * var attachmentList = this.workContext.getAttachmentList();
     */
    "getAttachmentList": function(){return JSON.parse(bind.java_workContext.getAttachmentList());},   //첨부파일 객체 배열 문자열

    /**
     * @summary 선택 가능한 라우트 객체 배열 문자열 조회(진행 이벤트에서 현재 work 상태에 따라 진행 중 선택 가능한 라우트 목록을 가져옴).
     * @method getRouteList
     * @static
     * @return {String[]} 경로 문자열 배열.
     * @o2syntax
     * var routeList = this.workContext.getRouteList();
     */
    "getRouteList": function(){return JSON.parse(bind.java_workContext.getRouteList());},      //선택 가능한 라우트 객체 배열 문자열(진행 이벤트에서 현재 work 상태에 따라 진행 중 선택 가능한 라우트 목록을 가져옴)

    /**
     * @summary 프로세스 인스턴스 제목 재설정
     * @method setTitle
     * @static
     * @param {String} title - 제목 문자열.
     * @o2syntax
     * this.workContext.setTitle(title);
     * @example
     * this.workContext.setTitle("제목");
     */
    "setTitle": function(title){bind.java_workContext.setTitle(title);},                       //title 설정


    "getControl": function(){return null;},
    "getInquiredRouteList": function(){return null;}
};
bind.workContent = bind.workContext;
//person, 직접 주입, oauth 설정 및 기본 토큰 생성 스크립트에서 사용

/**
 * 프로세스 이벤트, 프로세스 라우트 이벤트, 프로세스 활동 이벤트에서 this.data로 프로세스 인스턴스의 업무 데이터를 가져옵니다(콘텐츠 관리에는 백엔드 스크립트 없음).<br/>
 * 이 데이터는 일반적으로 사용자가 만든 양식으로 수집되며, 스크립트로 생성하고 추가·삭제·수정·조회할 수도 있습니다.<br/>
 * data 객체는 기본적으로 JSON 객체이며, JSON 객체에 접근하는 방법으로 data 객체의 모든 데이터에 접근할 수 있습니다.
 * @module server.data
 * @o2cn 프로세스 데이터
 * @o2category server.process
 * @o2ordernumber 205
 * @example
 * //폼 또는 프로세스의 각 내장 스크립트에서 this를 통해 현재 인스턴스의 업무 데이터를 가져올 수 있습니다. 예:
 * var data = this.data;
 * @borrows module:data#[property] as [property]
 */
//bind.data = this.java_data;  //업무 데이터 data, 직접 주입
//java 객체 data를 래핑하여 javascript 객체와 호환
(function (bind) {
    Object.prototype.containsKey = function(key){
        return Object.keys(this).indexOf(key)!==-1;
    }
    Object.prototype.put = function(key, value){
        this[key] = value;
    }
    var javaClass;
    try {
        javaClass = {
            map: java.util.Map,
            array: java.util.ArrayList,
            work: com.x.processplatform.core.entity.content.Data$DataWork
        }
    }catch (e){
        javaClass = {
            map: Object,
            array: Array,
            work: new Function()
        }
    }
    function proxyWork(data) {
        return (data instanceof javaClass.work) && JSON.parse(data.toString());
    }
    function proxyJavaObject(data) {
        return (data instanceof javaClass.map) && new ProxyData(data);
    }
    function proxyArray(data) {
        return ((data instanceof javaClass.array) || typeOf(data) === "array") && createProxyArray(data);
    }
    function createProxyArray(data) {
        var arr = [];
        data.forEach(function (d) {
            arr.push(createProxyData(d));
        });
        return arr;
        // return data.map(function (d) {
        //     return createProxyData(d);
        // });
    }
    function createProxyData(data) {
        var proxyData;
        [proxyJavaObject, proxyArray, proxyWork].some(function (fun) {
            return proxyData = fun(data);
        });
        return proxyData || data;
    }

    function _getter(key, tData, proxy) {
        return function () {
            if (!proxy.proxyData.hasOwnProperty(key)) {
                proxy.proxyData[key] = createProxyData(tData[key], proxy);
            }
            return proxy.proxyData[key];
        }
    }
    function _setter(key, data, proxy) {
        return function (value) {
            proxy.proxyData[key] = createProxyData(value, proxy);
            data.put(key, value);
            // data[key] = value;
        }
    }
    function _addData(data, proxy) {
        return function (key, value) {
            if (proxy.hasOwnProperty(key)) {
                proxy[key] = value;
            } else {
                data.put(key, value);
                //data[key] = value;
                Object.defineProperty(proxy, key, {
                    configurable: true,
                    enumerable: true,
                    get: _getter(key, data, proxy),
                    set: _setter(key, data, proxy)
                })
            }
        }
    }
    function _delData(data, proxy) {
        return function (key) {
            if (proxy.hasOwnProperty(key)) {
                data.remove(key);
                delete proxy[key];
            }
        }
    }
    function isProxyData(data){
        return ProxyData.prototype.isPrototypeOf(data);
    }
    function commitArray(arrList, proxy){
        proxy.forEach(function(d, i){
            if (Array.isArray(d)){
                if (arrList.length<=i) arrList.add(new java.util.ArrayList());
                commitArray(arrList[i], d)
            }else if (isProxyData(d)){
                d.commit();
            }else if (d instanceof Object) {
                if (arrList.length<=i) arrList.add(new java.util.LinkedHashMap());
                commitObject(arrList[i], d)
            }else{
                if (arrList.length<=i){
                    arrList.add(d);
                }else if (arrList[i]!==d){
                    arrList[i]=d;
                }
            }
        });
    }
    function commitObject(map, proxy){
        Object.keys(proxy).forEach(function(key){
            var d = proxy[key];
            if (Array.isArray(d)){
                if (!map.containsKey(key)) map.put(key, new java.util.ArrayList());
                commitArray(map[key], d)
            }else if (isProxyData(d)){
                d.commit();
            }else if (d instanceof Object) {
                if (!map.containsKey(key)) map.put(key, new java.util.LinkedHashMap());
                commitObject(map[key], d)
            }else{
                if (!map.containsKey(key) || map[key] !== d) map.put(key, d);
            }

        });
    }
    function commitData(data, proxy) {
        Object.keys(proxy).forEach(function (key) {
            var d = proxy[key];
            if (isProxyData(d)){
                d.commit();
            }else if (Array.isArray(d)) {
                if (!data.containsKey(key)) data[key] = new java.util.ArrayList();
                commitArray(data[key], d)
            }else if (!data.containsKey(key)){
                data[key] = d;
            }
        })
    }

    function ProxyData(tData) {
        Object.defineProperty(this, "proxyData", {
            value: {}
        });
        var _self = this;

        var keys = tData.keySet();
        var o = {};
        keys.forEach(function (key) {
            o[key] = {
                configurable: true,
                enumerable: true,
                get: _getter(key, tData, _self),
                set: _setter(key, tData, _self)
            }
        });
        o.add = {
            value: _addData(tData, _self)
        };
        o.del = {
            value: _delData(tData, _self)
        };
        o.commit = {
            value: function(){
                commitData(tData, _self);
            }
        };
        Object.defineProperties(this, o);
    }

    Object.defineProperty(bind, "data", {
        configurable: true,
        enumerable: true,
        get: function(){
            if (!this.__data__) this.__data__ = (bind.java_data) && new ProxyData(bind.java_data);
            return this.__data__;
        }
    })
})(this);

//프로세스 호출 스크립트, 데이터 스크립트--------------------------------------------
//{
//	data:{},
//	application:"",
//	process:"",
//	identity: "",
//	attachmentList: [],
//	title: "",
//	processing: true
//}
//추후 return으로 assignData 반환 예정,
/**
 * 프로세스 설정의 프로세스 호출 활동 「데이터 스크립트」에서 사용하며, assignData 객체로 호출할 프로세스의 관련 정보와 호출되는 프로세스 인스턴스에 전달할 업무 데이터를 가져올 수 있습니다.<br>
 * 업무 데이터를 수정한 뒤 assignData의 set 메서드로 호출되는 프로세스 인스턴스에 전달할 수도 있습니다.<br/>
 * @o2range 프로세스 설정-프로세스 호출 활동의 「데이터 스크립트」에서 사용 가능
 * @module server.assignData
 * @o2cn 프로세스 인스턴스 업무 데이터
 * @o2category server.process
 * @o2ordernumber 210
 * @example
 * //프로세스 호출 활동의 「데이터 스크립트」에서 아래 코드로 업무 데이터를 수정해 호출되는 프로세스 인스턴스에 전달합니다:
 * var data = this.assignData.get();
 * data.data.parentProcessData = "상위 프로세스 인스턴스 정보";
 * this.assignData.set(data);
 * @example
 * <caption>
 *    assignData.set 메서드는 이전 버전과의 호환을 위한 것입니다.<br>
 *    <b>json 객체를 return하는 방식으로 data 내용을 설정할 것을 권장합니다</b>
 * </caption>
 * //json 객체를 return하는 방식으로 assignData.set 메서드를 대체할 수도 있습니다
 * var data = this.assignData.get();
 * data.data.parentProcessData = "상위 프로세스 인스턴스 정보";
 * return data;
 */
bind.assignData = {     //java_assignData 앱 호출 활동에서 생성한 프로세스 인스턴스의 업무 데이터 처리 객체, get set 메서드
    _data: null,
    /**
     * @summary 호출할 프로세스의 관련 정보와 호출되는 프로세스 인스턴스에 전달할 업무 데이터 조회.
     * @method get
     * @methodOf module:server.assignData
     * @static
     * @return {Object} 호출되는 프로세스 정보 및 전달할 업무 데이터.
     * <pre><code class='language-js'>{
     *        "application": "application id",  //호출되는 앱 id
     *        "process": "process id",          //호출되는 프로세스 id
     *        "identity": "xxx@xxx@I",          //호출되는 프로세스의 시작 신분
     *        "title": "title",                 //호출되는 프로세스 인스턴스 제목
     *        "attachmentList": [],             //호출되는 프로세스 인스턴스에 전달할 첨부파일 객체
     *        "data": {},                       //호출되는 프로세스 인스턴스에 전달할 업무 데이터
     *        "attachmentSoftCopy"              //true이면 첨부파일을 복사하지 않음. 기본값 false  @todo
     * }</code></pre>
     * @o2syntax
     * var data = this.assignData.get();
     */
    "get": function(){
        this.data = JSON.parse(bind.java_assignData.get());
        return this.data;
    },
    /**
     * @summary 수정한 assignData 객체 설정(set 메서드는 초기 버전과의 호환용. return data; 방식으로 json 객체를 직접 반환하는 것을 권장)
     * @method set
     * @methodOf module:server.assignData
     * @static
     * @param {Object} [data] 설정할 assignData 객체. 일반적으로 assignData.get()으로 가져와 필요한 부분을 수정한 객체.
     * @o2syntax
     * this.assignData.set(data);
     * @deprecated set 메서드는 더 이상 권장하지 않습니다. json 객체나 배열을 return하는 방식으로 data를 설정하십시오.
     * @example
     * var data = this.assignData.get();
     * data.data.parentProcessData = "상위 프로세스 인스턴스 정보";
     * return data;
     */
    "set": function(data){
        bind.java_assignData.set(JSON.stringify(data || this.data));
    }
};
Object.defineProperties(bind.assignData, {"data": {
        "configurable": true,
        "get": function(){
            if (this._data) return this._data;
            return JSON.parse(bind.java_assignData.get());
        },
        "set": function(v){this._data = v;}
    }});
//--------------------------------------------------------------

//서비스 호출 활동, 관련 스크립트--------------------------------------------
//호출 활동의 파라미터 java_jaxwsParameters webservice 호출;  java_jaxrsParameterss rest 호출
//추후 return으로 json(jaxrs) 또는 배열(jaxws) 반환 예정
/**
 * 프로세스 설정의 서비스 호출 활동 「파라미터 스크립트」에서 사용하며, jaxrs와 jaxws 두 서비스 호출 방식에 파라미터를 전달합니다.<br>
 * 호출 유형이 jaxrs일 때: parameters.put과 parameters.remove 메서드 사용 가능<br/>
 * 호출 유형이 jaxws일 때: parameters.add와 parameters.remove 메서드 사용 가능<br/>
 * @o2range 프로세스 설정-서비스 호출 활동의 「파라미터 스크립트」에서 사용 가능
 * @module server.parameters
 * @o2cn 서비스 호출 파라미터
 * @o2category server.process
 * @o2ordernumber 215
 * @deprecated parameters 객체는 더 이상 권장하지 않습니다. json 객체나 배열을 return하는 방식으로 파라미터를 설정하십시오.
 * @example
 * //jaxrs 방식 서비스 호출 활동의 파라미터 스크립트에서
 * //rest 서비스 주소가 xxx/{id}/xx/{name}이면 id와 name 두 파라미터를 전달해야 하며, 다음 코드를 사용할 수 있습니다:
 * this.parameters.put("id", "id value");
 * this.parameters.put("name", "name value");
 *
 * //또는
 * this.parameters.put({
 *     "id": "id value",
 *     "name": "name value"
 * });
 *
 * @example
 * //jaxws 방식 서비스 호출 활동의 파라미터 스크립트에서
 * //세 개의 파라미터를 전달해야 하면 다음 코드를 사용할 수 있습니다:
 * this.parameters.add("파라미터1");
 * this.parameters.add("파라미터2");
 * this.parameters.add("파라미터3");
 *
 * //또는
 * this.parameters.add(["파라미터1", "파라미터2", "파라미터3"]);
 *
 * @example
 * <caption>
 *    위 두 예시에서는 parameters 객체로 파라미터를 수집했는데, 이는 주로 이전 버전과의 호환을 위한 것입니다.<br>
 *    <b>json 객체나 배열을 return하는 방식으로 파라미터를 설정할 것을 더 권장합니다</b>
 * </caption>
 * //위 두 예시에서는 parameters 객체로 파라미터를 수집함
 * //더 나은 방식: json 객체나 배열을 return하는 방식으로 파라미터 설정
 * //jaxrs 방식의 경우:
 * return {
 *     "id": "id value",
 *     "name": "name value"
 * }
 *
 * //jaxws 방식의 경우:
 * return ["파라미터1", "파라미터2", "파라미터3"];
 */
bind.parameters = {
    /**
     * @summary jaxws 방식 서비스 호출 활동의 「파라미터 스크립트」에서 parameters.add로 파라미터 설정.
     * @method add
     * @methodOf module:server.parameters
     * @static
     * @param {Any|Array} [value] 설정할 파라미터 값.
     * @o2syntax
     * this.parameters.add(value);
     * @deprecated 권장하지 않습니다. 배열을 return하는 방식으로 파라미터를 설정하십시오. 예:
     * <pre><code class='language-js'>return ["파라미터1", "파라미터2", "파라미터3"];</code></pre>
     */
    "add": function(value){
        try{
            if (bind.java_jaxwsParameters){
                if (Array.isArray(value)){
                    value.forEach(function(v){
                        bind.java_jaxwsParameters.add(v);
                    });
                }else{
                    bind.java_jaxwsParameters.add(value);
                }
            }
        }catch(e){}
    },
    /**
     * @summary jaxrs 방식 서비스 호출 활동의 「파라미터 스크립트」에서 parameters.put으로 파라미터 설정.
     * @method put
     * @methodOf module:server.parameters
     * @static
     * @param {String|Object} [name] 설정할 파라미터 key 또는 파라미터 json 객체.
     * @param {String|Number|boolean} [value] 설정할 파라미터 값.
     * @o2syntax
     * this.parameters.put(name, value);
     * this.parameters.put(obj);
     * @deprecated 권장하지 않습니다. json 객체를 return하는 방식으로 파라미터를 설정하십시오. 예:
     * <pre><code class='language-js'>return {
     *     "id": "id value",
     *     "name": "name value"
     * };</code></pre>
     */
    "put": function(name, value){
        try{
            if ((typeof name)==="object"){
                var _keys = Object.keys(name);
                for (var i=0; i<_keys.length; i++){
                    if (bind.java_jaxrsParameters) bind.java_jaxrsParameters.put(_keys[i], name[_keys[i]]);
                }
            }else{
                if (bind.java_jaxrsParameters) bind.java_jaxrsParameters.put(name, value);
            }
        }catch(e){}
    },
    /**
     * @summary 서비스 호출 활동의 「파라미터 스크립트」에서 설정한 파라미터 삭제.
     * @method remove
     * @methodOf module:server.parameters
     * @static
     * @param {String|Number} [name] jaxrs 방식 서비스 호출 활동은 삭제할 파라미터의 key, jaxws 방식 서비스 호출 활동은 삭제할 파라미터의 인덱스를 전달.
     * @o2syntax
     * this.parameters.remove(name);
     * @deprecated 권장하지 않음
     */
    "remove": function(name){
        try{
            if (bind.java_jaxwsParameters) bind.java_jaxwsParameters.remove(name);
            if (bind.java_jaxrsParameters) bind.java_jaxrsParameters.remove(name);
        }catch(e){}
    }
};

//java_jaxrsBody //호출 활동의 body
//추후 return으로 반환 예정
/**
 * 프로세스 설정의 서비스 호출 활동 「메시지 본문 스크립트」에서 사용하며, jaxrs 방식 서비스 호출 활동에서만 사용할 수 있습니다.<br>
 * @o2range 프로세스 설정-서비스 호출 활동의 「메시지 본문 스크립트」, 서비스 프로토콜은 jaxrs
 * @module server.body
 * @o2cn 서비스 호출 메시지 본문
 * @o2category server.process
 * @o2ordernumber 220
 * @deprecated body 객체는 더 이상 권장하지 않습니다. json 객체를 return하는 방식으로 body를 설정하십시오. 예:
 * <pre><code class='language-js'>return {
 *     "key1": "value1",
 *     "key2": "value2",
 *     "key3": "value3"
 * };</code></pre>
 * @example
 * //jaxrs 서비스 호출의 메시지 본문 설정
 * this.body.set({
 *     "key1": "value1",
 *     "key2": "value2",
 *     "key3": "value3"
 * })
 *
 * //또는 설정
 * this.body.set("this is body data");
 *
 * @example
 * <caption>
 *    body.set 메서드는 이전 버전과의 호환을 위한 것입니다.<br>
 *    <b>json 객체를 return하는 방식으로 메시지 본문 내용을 설정할 것을 권장합니다</b>
 * </caption>
 * return {
 *      "key1": "value1",
 *     "key2": "value2",
 *     "key3": "value3"
 * };
 *
 * //또는
 * return "this is body data";
 */
bind.body = {
    /**
     * @summary 서비스 호출 활동의 「메시지 본문 스크립트」에서 메시지 본문 내용 설정.
     * @method set
     * @methodOf module:server.body
     * @static
     * @param {String|Object} [data] 메시지 본문 내용.
     * @o2syntax
     * this.body.set(data);
     * @deprecated 권장하지 않습니다. json 객체나 배열을 return하는 방식으로 body를 설정하십시오.
     */
    "set": function(data){
        if ((typeof data)==="string"){
            bind.java_jaxrsBody.set(data);
        }
        if ((typeof data)==="object"){
            bind.java_jaxrsBody.set(JSON.stringify(data));
        }
    }
};

//java_jaxrsHeaders //호출 활동의 headers
//추후 return으로 반환 예정
/**
 * 프로세스 설정의 서비스 호출 활동 「메시지 헤더 스크립트」에서 사용하며, jaxrs 방식 서비스 호출 활동에서만 사용할 수 있습니다.<br>
 * @o2range 프로세스 설정-서비스 호출 활동의 「메시지 헤더 스크립트」, 서비스 프로토콜은 jaxrs
 * @module server.headers
 * @o2cn 서비스 호출 메시지 헤더
 * @o2category server.process
 * @o2ordernumber 225
 * @deprecated headers 객체는 더 이상 권장하지 않습니다. json 객체를 return하는 방식으로 headers를 설정하십시오. 예:
 * <pre><code class='language-js'>return {
 *     "Content-Type": "application/x-www-form-urlencoded"，
 *     "Accept-Language": "en"
 * };</code></pre>
 * @example
 * //jaxrs 서비스 호출의 메시지 헤더 설정
 * this.headers.put("Content-Type", "application/x-www-form-urlencoded");
 *
 * //또는
 * this.headers.put({
 *      "Content-Type": "application/x-www-form-urlencoded"，
 *      "Accept-Language": "en"
 * });
 *
 * @example
 * <caption>
 *    headers.put 메서드는 이전 버전과의 호환을 위한 것입니다.<br>
 *    <b>json 객체를 return하는 방식으로 메시지 헤더 내용을 설정할 것을 권장합니다</b>
 * </caption>
 * return {
 *      "Content-Type": "application/x-www-form-urlencoded"，
 *      "Accept-Language": "en"
 * };
 */
bind.headers = {
    /**
     * @summary jaxrs 방식 서비스 호출 활동의 「메시지 헤더 스크립트」에서 headers.put으로 메시지 헤더 설정.
     * @method add
     * @methodOf module:server.headers
     * @static
     * @param {String|Object} [name] 설정할 메시지 헤더 이름 또는 메시지 헤더 json 객체.
     * @param {String} [value] 설정할 메시지 헤더 값.
     * @o2syntax
     * this.headers.put(name, value);
     * this.headers.put(obj);
     * @deprecated 권장하지 않습니다. json 객체를 return하는 방식으로 headers를 설정하십시오. 예:
     * <pre><code class='language-js'>return {
     *     "Content-Type": "application/x-www-form-urlencoded"，
     *     "Accept-Language": "en"
     * };</code></pre>
     */
    "put": function(name, value){
        try{
            if ((typeof name)==="object"){
                var _keys = Object.keys(name);
                for (var i=0; i<_keys.length; i++){
                    if (bind.java_jaxrsHeaders) bind.java_jaxrsHeaders.put(_keys[i], name[_keys[i]]);
                }
            }else{
                if (bind.java_jaxrsHeaders) bind.java_jaxrsHeaders.put(name, value);
            }
        }catch(e){}
    },
    /**
     * @summary jaxrs 방식 서비스 호출 활동의 「메시지 헤더 스크립트」에서 설정한 메시지 헤더 삭제.
     * @method remove
     * @methodOf module:server.headers
     * @static
     * @param {String} [name] 삭제할 메시지 헤더 이름.
     * @o2syntax
     * this.headers.remove(name);
     * @deprecated 권장하지 않음
     */
    "remove": function(name){
        try{
            if (bind.java_jaxrsHeaders) bind.java_jaxrsHeaders.remove(name);
        }catch(e){}
    }
};


//java_jaxrsResponse, type()
//java_jaxrsResponse, get() 문자열

//java_jaxwsResponse  처리 없이 바로 반환
/**
 * 프로세스 설정의 서비스 호출 활동 「응답 스크립트」에서 사용하며, 서비스 호출 후 받은 응답을 나타냅니다.(주의: 호출 방식이 비동기이면 「응답 스크립트」는 실행되지 않습니다)<br>
 * @o2range 프로세스 설정-서비스 호출 활동의 「응답 스크립트」
 * @module server.response
 * @o2cn 서비스 호출 응답
 * @o2category server.process
 * @o2ordernumber 230
 * @o2syntax
 * var res = this.response;
 */
bind.response = {};
/**
 * @summary jaxrs 방식 서비스 호출 활동의 요청 반환 상태. 가능한 값은 네 가지:
 * <pre><code class="language-js">success : 요청 성공
 * warn : 플랫폼 내부 서비스를 호출할 때 요청은 성공했지만 경고 발생
 * error : 요청 호출 오류
 * connectFatal : 네트워크 연결 오류</code></pre>
 * @member {String} status
 * @memberOf server.module:response
 * @o2syntax
 * var status = this.response.status;
 */
Object.defineProperty(bind.response, "status", {enumerable: true,configurable: true,
    get: function(){ return (bind.java_jaxrsResponse) ? bind.java_jaxrsResponse.type() : ""; }
});
/**
 * @summary 요청 반환 값. jaxrs 서비스는 응답 텍스트를 받고, jaxws 서비스는 응답 유형이 서비스 반환에 따름.
 * @member {String|Any} value
 * @memberOf server.module:response
 * @o2syntax
 * var value = this.response.value;
 * @example
 * //json 객체 반환 정보 처리
 * var value = this.response.value;
 * var resData = JSON.parse(value);
 */
Object.defineProperty(bind.response, "value", {enumerable: true,configurable: true,
    get: function(){
        if (bind.java_jaxrsResponse){
            return bind.java_jaxrsResponse.get();
        }else if(_self.jaxwsResponse){
            return bind.java_jaxwsResponse;
        }
    }
});
/**
 * @summary 요청 반환 값 조회. jaxrs 서비스는 응답 데이터를 가능한 한 json 객체로 변환하고, json 객체로 해석할 수 없으면 텍스트를 반환. jaxws 서비스는 응답 유형이 서비스 반환에 따름.
 * @method get
 * @methodOf server.module:response
 * @static
 * @return {Object|String|Any} 응답 내용.
 * @o2syntax
 * var res = this.response.get();
 */
Object.defineProperty(bind.response, "get", {enumerable: true,configurable: true,
    get: function(){
        return function(){
            if (bind.java_jaxrsResponse){
                var value = bind.java_jaxrsResponse.get();
                if (JSON.validate(value)){
                    return JSON.decode(value);
                }
                return value;
            }else if(bind.java_jaxwsResponse){
                return bind.java_jaxwsResponse || null
            }
        };
    }
});
//-----------------------------------------------------------------------------------


//백엔드에는 form 없음
bind.form = null;

//java_requestText, 서비스 활동, 요청 데이터 문자열
/**
 * 프로세스 설정의 서비스 활동 「서비스 응답 스크립트」에서 사용하며, 서비스를 요청한 요청 객체를 나타냅니다.<br>
 * @o2range 프로세스 설정-서비스 활동의 「응답 스크립트」
 * @module server.request
 * @o2cn 서비스 호출 요청 객체
 * @o2category server.process
 * @o2ordernumber 235
 * @o2syntax
 * var res = this.request;
 */
bind.request = {
    /**
     * @summary 요청 body 내용 조회.
     * @method getBody
     * @methodOf server.module:request
     * @static
     * @return {Object|String} 요청 body 내용. json으로 변환 가능하면 json 객체를, 아니면 요청 내용 텍스트를 반환.
     * @o2syntax
     * var req = this.request.getBody();
     */
    "getBody": function(){
        try{
            return JSON.parse(bind.java_requestText);
        }catch(e){
            return bind.java_requestText;
        }
    }
}
/**
 * @summary 요청 body의 원본 내용 조회.
 * @member {String} text
 * @memberOf server.module:request
 * @o2syntax
 * var req = this.request.text;
 */
Object.defineProperties(bind, {
    "text": {
        "configurable": true,
        "get": function(){return bind.java_requestText;}
    }
});

//활동 기한 계산 객체, 프로세스 기한은 제거 필요
//java_expire,
//스크립트 반환 JSON 데이터:
//{
//    hour: 3                     //몇 시간 후 기한 초과
//    workHour: 5             //몇 근무 시간 후 기한 초과
//    date: '2016-08-01'   //지정 시간 도달 후 기한 초과
//}

//지정한 기한 초과 시간 설정:
//this.expire.setDate([Date or String] date)
//몇 시간 후 기한 초과 설정:
//this.expire.setHour([Int] hour)
//몇 근무 시간 후 기한 초과 설정:
//this.expire.setWorkHour([Int] hour)
/**
 * 프로세스 설정의 인공 활동 「기한 스크립트」에서 기한 초과 시간을 설정합니다. 시간 수, 근무 시간 수, 지정 시점으로 기한 초과를 설정할 수 있으며, 모두 설정하면 우선순위는 근무 시간>시간>시간점입니다<br>
 * @o2range 프로세스 설정-인공 활동의 「기한 스크립트」
 * @module server.expire
 * @o2cn 기한 초과 시간 설정
 * @o2category server.process
 * @o2ordernumber 240
 * @deprecated expire 객체는 더 이상 권장하지 않습니다. json 객체를 return하는 방식으로 기한 초과 시간을 설정하십시오.
 * @example
 * //기한을 결재 대기 생성 후 5시간으로 설정
 * this.expire.setHour(5);
 *
 * //기한을 결재 대기 생성 후 5 근무 시간으로 설정(근무 시간만 계산)
 * this.expire.setWorkHour(5);
 *
 * //기한을 지정 시간으로 설정. 예: 업무 데이터에 설정된 처리 기한(processingTime)
 * this.expire.setDate(this.data.processingTime);
 *
 * @example
 * <caption>
 *    expire 객체는 이전 버전과의 호환을 위한 것입니다.<br>
 *    <b>json 객체를 직접 반환하여 기한 초과 시간을 설정할 것을 권장합니다</b>
 * </caption>
 * //기한을 결재 대기 생성 후 5시간으로 설정
 * return {"hour": 5};
 *
 * //기한을 결재 대기 생성 후 5 근무 시간으로 설정(근무 시간만 계산)
 * return {"workHour": 5};
 *
 * //기한을 지정 시간으로 설정. 예: 업무 데이터에 설정된 처리 기한(processingTime)
 * return {"date": this.data.processingTime};
 */
bind.expire = {
    /**
     * @summary 기한 초과 시간 수 설정.
     * @method setHour
     * @methodOf server.module:expire
     * @static
     * @param {Number} [hour] 기한 초과 시간 수.
     * @deprecated 권장하지 않습니다. json 객체를 return하는 방식으로 기한 초과 시간을 설정하십시오. 예:
     * <pre><code class='language-js'>return {"hour": 5}</code></pre>
     */
    "setHour": function(hour){
        try{bind.java_expire.setHour(hour);}catch(e){}
    },
    /**
     * @summary 기한 초과 근무 시간 수 설정.
     * @method setWorkHour
     * @methodOf server.module:expire
     * @static
     * @param {Number} [hour] 기한 초과 근무 시간 수.
     * @deprecated 권장하지 않습니다. json 객체를 return하는 방식으로 기한 초과 시간을 설정하십시오. 예:
     * <pre><code class='language-js'>return {"workHour": 5}</code></pre>
     */
    "setWorkHour": function(hour){
        try{bind.java_expire.setWorkHour(hour);}catch(e){}
    },
    /**
     * @summary 기한 초과 시간 설정.
     * @method setDate
     * @methodOf server.module:expire
     * @static
     * YYYY-MM-DD HH:mm:SS
     * @param {String} [date] 날짜 시간을 나타내는 문자열, 형식은 다음과 같음:
     * <pre><code class="language-js">yyyy-MM-dd HH:mm:ss   //예: 2021-09-12 18:26:51</code></pre>
     * @deprecated 권장하지 않습니다. json 객체를 return하는 방식으로 기한 초과 시간을 설정하십시오. 예:
     * <pre><code class='language-js'>return {"date": "2021-09-12 18:26:51"}</code></pre>
     */
    "setDate": function(date){
        try{bind.java_expire.setDate(date);}catch(e){}
    }
};

/**
 * 프로세스 호출 활동에서 프로세스 대기를 사용하면 "하위 프로세스 성공 후", "하위 프로세스 취소 후", "하위 프로세스 완료 후" 세 이벤트 스크립트에서 embedData 객체에 접근할 수 있습니다<br/>
 * embedData 객체는 호출된 하위 프로세스의 업무 데이터이며 JSON과 유사한 객체로, JSON 객체에 접근하는 방법으로 embedData 객체의 모든 데이터에 접근할 수 있습니다.<br/>
 * embedData의 json 텍스트가 필요하면 embedData.toString() 메서드를 사용하십시오.<b>주의: JSON.stringify() 메서드는 embedData 객체에 사용할 수 없습니다</b><br>
 * work 객체의 embedCompleted 값으로 호출된 하위 프로세스가 정상 완료되었는지 판단할 수 있습니다. cancel end terminate
 * <pre><code class='language-js'>
 *  var embedStatus = this.workContext.getWork().embedCompleted;
 *  if (embedStatus=="end"){
 *      //호출된 하위 프로세스가 정상적으로 종료 활동까지 진행됨
 *  }
 *  if (embedStatus=="cancel"){
 *      //호출된 하위 프로세스가 취소 활동으로 진행됨
 *  }
 *  if (embedStatus=="terminate"){
 *      //호출된 하위 프로세스가 종료됨
 *  }
 * </code></pre>
 * @o2range 프로세스 설정-프로세스 호출 활동에서 프로세스 대기를 사용할 때 "하위 프로세스 성공 후", "하위 프로세스 취소 후", "하위 프로세스 완료 후" 세 이벤트에서 사용 가능
 * @module server.embedData
 * @o2cn 호출 활동의 하위 프로세스 업무 데이터
 * @o2category server.process
 * @o2ordernumber 205
 * @example
 * //폼 또는 프로세스의 각 내장 스크립트에서 this를 통해 현재 인스턴스의 업무 데이터를 가져올 수 있습니다. 예:
 * var embedData = this.embedData;
 */

var o= {
    "context": { "configurable": true, "get": function(){return ((bind.java_resources) ? bind.java_resources.getContext() : null)} },
    "applications": { "configurable": true, "get": function(){return ((bind.java_resources) ? bind.java_resources.getApplications() : null)} },
    "organization": { "configurable": true, "get": function(){return ((bind.java_resources) ? bind.java_resources.getOrganization() : null)} },
    // "service": { "configurable": true, "get": function(){return ((bind.java_resources) ? bind.java_resources.getWebservicesClient() : null)} },
    "currentPerson": { "configurable": true, "get": function(){return (bind.java_effectivePerson || null)} },
    "effectivePerson": { "configurable": true, "get": function(){return (bind.java_effectivePerson || null)} },
    "resources": { "configurable": true, "get": function(){return (bind.java_resources || null)} }
}
Object.defineProperties(bind, o);
/* engine 정리 */
this.engine = null;
