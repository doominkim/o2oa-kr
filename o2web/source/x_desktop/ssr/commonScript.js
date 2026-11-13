//mootools
var GLOBAL_ITEMS=function(){var t=[];for(var e in this)t.push(e);return t}();!function(){this.MooTools={version:"1.5.0dev",build:"%build%"};var t=this.typeOf=function(t){if(null==t)return"null";if(null!=t.$family)return t.$family();if(t.nodeName){if(1==t.nodeType)return"element";if(3==t.nodeType)return/\S/.test(t.nodeValue)?"textnode":"whitespace"}else if("number"==typeof t.length){if(t.callee)return"arguments";if("item"in t)return"collection"}return typeof t},e=(this.instanceOf=function(t,e){if(null==t)return!1;for(var n=t.$constructor||t.constructor;n;){if(n===e)return!0;n=n.parent}return!!t.hasOwnProperty&&t instanceof e},this.Function),n=!0;for(var r in{toString:1})n=null;n&&(n=["hasOwnProperty","valueOf","isPrototypeOf","propertyIsEnumerable","toLocaleString","toString","constructor"]),e.prototype.overloadSetter=function(t){var e=this;return function(r,i){if(null==r)return this;if(t||"string"!=typeof r){for(var a in r)e.call(this,a,r[a]);if(n)for(var o=n.length;o--;)a=n[o],r.hasOwnProperty(a)&&e.call(this,a,r[a])}else e.call(this,r,i);return this}},e.prototype.overloadGetter=function(t){var e=this;return function(n){var r,i;if("string"!=typeof n?r=n:arguments.length>1?r=arguments:t&&(r=[n]),r){i={};for(var a=0;a<r.length;a++)i[r[a]]=e.call(this,r[a])}else i=e.call(this,n);return i}},e.prototype.extend=function(t,e){this[t]=e}.overloadSetter(),e.prototype.implement=function(t,e){this.prototype[t]=e}.overloadSetter();var i=Array.prototype.slice;e.from=function(e){return"function"==t(e)?e:function(){return e}},e.convert=e.from,Array.from=function(e){return null==e?[]:a.isEnumerable(e)&&"string"!=typeof e?"array"==t(e)?e:i.call(e):[e]},Array.convert=Array.from,Number.from=function(t){var e=parseFloat(t);return isFinite(e)?e:null},Number.convert=Number.from,String.from=function(t){return t+""},String.convert=String.from,e.implement({hide:function(){return this.$hidden=!0,this},protect:function(){return this.$protected=!0,this}});var a=this.Type=function(e,n){if(e){var r=e.toLowerCase();a["is"+e]=function(e){return t(e)==r},null!=n&&(n.prototype.$family=function(){return r}.hide())}return null==n?null:(n.extend(this),n.$constructor=a,n.prototype.$constructor=n,n)},o=Object.prototype.toString;a.isEnumerable=function(t){return null!=t&&"number"==typeof t.length&&"[object Function]"!=o.call(t)};var s={},u=function(e){var n=t(e.prototype);return s[n]||(s[n]=[])},c=function(e,n){if(!n||!n.$hidden){for(var r=u(this),a=0;a<r.length;a++){var o=r[a];"type"==t(o)?c.call(o,e,n):o.call(this,e,n)}var s=this.prototype[e];null!=s&&s.$protected||(this.prototype[e]=n),null==this[e]&&"function"==t(n)&&l.call(this,e,(function(t){return n.apply(t,i.call(arguments,1))}))}},l=function(t,e){if(!e||!e.$hidden){var n=this[t];null!=n&&n.$protected||(this[t]=e)}};a.implement({implement:c.overloadSetter(),extend:l.overloadSetter(),alias:function(t,e){c.call(this,t,this.prototype[e])}.overloadSetter(),mirror:function(t){return u(this).push(t),this}}),new a("Type",a);var h=function(t,e,n){var r=e!=Object,i=e.prototype;r&&(e=new a(t,e));for(var o=0,s=n.length;o<s;o++){var u=n[o],c=e[u],l=i[u];c&&c.protect(),r&&l&&e.implement(u,l.protect())}if(r){var f=i.propertyIsEnumerable(n[0]);e.forEachMethod=function(t){if(!f)for(var e=0,r=n.length;e<r;e++)t.call(i,i[n[e]],n[e]);for(var a in i)t.call(i,i[a],a)}}return h};h("String",String,["charAt","charCodeAt","concat","indexOf","lastIndexOf","match","quote","replace","search","slice","split","substr","substring","trim","toLowerCase","toUpperCase"])("Array",Array,["pop","push","reverse","shift","sort","splice","unshift","concat","join","slice","indexOf","lastIndexOf","filter","forEach","every","map","some","reduce","reduceRight"])("Number",Number,["toExponential","toFixed","toLocaleString","toPrecision"])("Function",e,["apply","call","bind"])("RegExp",RegExp,["exec","test"])("Object",Object,["create","defineProperty","defineProperties","keys","getPrototypeOf","getOwnPropertyDescriptor","getOwnPropertyNames","preventExtensions","isExtensible","seal","isSealed","freeze","isFrozen"])("Date",Date,["now"]),Object.extend=l.overloadSetter(),Date.extend("now",(function(){return+new Date})),new a("Boolean",Boolean),Number.prototype.$family=function(){return isFinite(this)?"number":"null"}.hide(),Number.extend("random",(function(t,e){return Math.floor(Math.random()*(e-t+1)+t)}));var f=Object.prototype.hasOwnProperty;Object.extend("forEach",(function(t,e,n){for(var r in t)f.call(t,r)&&e.call(n,t[r],r,t)})),Object.each=Object.forEach,Array.implement({forEach:function(t,e){for(var n=0,r=this.length;n<r;n++)n in this&&t.call(e,this[n],n,this)},each:function(t,e){return Array.forEach(this,t,e),this}});var d=function(e){switch(t(e)){case"array":return e.clone();case"object":return Object.clone(e);default:return e}};Array.implement("clone",(function(){for(var t=this.length,e=new Array(t);t--;)e[t]=d(this[t]);return e}));var g=function(e,n,r){switch(t(r)){case"object":"object"==t(e[n])?Object.merge(e[n],r):e[n]=Object.clone(r);break;case"array":e[n]=r.clone();break;default:e[n]=r}return e};Object.extend({merge:function(e,n,r){if("string"==t(n))return g(e,n,r);for(var i=1,a=arguments.length;i<a;i++){var o=arguments[i];for(var s in o)g(e,s,o[s])}return e},clone:function(t){var e={};for(var n in t)e[n]=d(t[n]);return e},append:function(t){for(var e=1,n=arguments.length;e<n;e++){var r=arguments[e]||{};for(var i in r)t[i]=r[i]}return t}}),["Object","WhiteSpace","TextNode","Collection","Arguments"].each((function(t){new a(t)}));var p=Date.now();String.extend("uniqueID",(function(){return(p++).toString(36)}))}(),Array.implement({every:function(t,e){for(var n=0,r=this.length>>>0;n<r;n++)if(n in this&&!t.call(e,this[n],n,this))return!1;return!0},filter:function(t,e){for(var n,r=[],i=0,a=this.length>>>0;i<a;i++)i in this&&(n=this[i],t.call(e,n,i,this)&&r.push(n));return r},indexOf:function(t,e){for(var n=this.length>>>0,r=e<0?Math.max(0,n+e):e||0;r<n;r++)if(this[r]===t)return r;return-1},map:function(t,e){for(var n=this.length>>>0,r=Array(n),i=0;i<n;i++)i in this&&(r[i]=t.call(e,this[i],i,this));return r},some:function(t,e){for(var n=0,r=this.length>>>0;n<r;n++)if(n in this&&t.call(e,this[n],n,this))return!0;return!1},clean:function(){return this.filter((function(t){return null!=t}))},invoke:function(t){var e=Array.slice(arguments,1);return this.map((function(n){return n[t].apply(n,e)}))},associate:function(t){for(var e={},n=Math.min(this.length,t.length),r=0;r<n;r++)e[t[r]]=this[r];return e},link:function(t){for(var e={},n=0,r=this.length;n<r;n++)for(var i in t)if(t[i](this[n])){e[i]=this[n],delete t[i];break}return e},contains:function(t,e){return-1!=this.indexOf(t,e)},append:function(t){return this.push.apply(this,t),this},getLast:function(){return this.length?this[this.length-1]:null},getRandom:function(){return this.length?this[Number.random(0,this.length-1)]:null},include:function(t){return this.contains(t)||this.push(t),this},combine:function(t){for(var e=0,n=t.length;e<n;e++)this.include(t[e]);return this},erase:function(t){for(var e=this.length;e--;)this[e]===t&&this.splice(e,1);return this},empty:function(){return this.length=0,this},flatten:function(){for(var t=[],e=0,n=this.length;e<n;e++){var r=typeOf(this[e]);"null"!=r&&(t=t.concat("array"==r||"collection"==r||"arguments"==r||instanceOf(this[e],Array)?Array.flatten(this[e]):this[e]))}return t},pick:function(){for(var t=0,e=this.length;t<e;t++)if(null!=this[t])return this[t];return null},hexToRgb:function(t){if(3!=this.length)return null;var e=this.map((function(t){return 1==t.length&&(t+=t),t.toInt(16)}));return t?e:"rgb("+e+")"},rgbToHex:function(t){if(this.length<3)return null;if(4==this.length&&0==this[3]&&!t)return"transparent";for(var e=[],n=0;n<3;n++){var r=(this[n]-0).toString(16);e.push(1==r.length?"0"+r:r)}return t?e:"#"+e.join("")}}),String.implement({test:function(t,e){return("regexp"==typeOf(t)?t:new RegExp(""+t,e)).test(this)},contains:function(t,e){return e?(e+this+e).indexOf(e+t+e)>-1:String(this).indexOf(t)>-1},trim:function(){return String(this).replace(/^\s+|\s+$/g,"")},clean:function(){return String(this).replace(/\s+/g," ").trim()},camelCase:function(){return String(this).replace(/-\D/g,(function(t){return t.charAt(1).toUpperCase()}))},hyphenate:function(){return String(this).replace(/[A-Z]/g,(function(t){return"-"+t.charAt(0).toLowerCase()}))},capitalize:function(){return String(this).replace(/\b[a-z]/g,(function(t){return t.toUpperCase()}))},escapeRegExp:function(){return String(this).replace(/([-.*+?^${}()|[\]\/\\])/g,"\\$1")},toInt:function(t){return parseInt(this,t||10)},toFloat:function(){return parseFloat(this)},hexToRgb:function(t){var e=String(this).match(/^#?(\w{1,2})(\w{1,2})(\w{1,2})$/);return e?e.slice(1).hexToRgb(t):null},rgbToHex:function(t){var e=String(this).match(/\d{1,3}/g);return e?e.rgbToHex(t):null},substitute:function(t,e){return String(this).replace(e||/\\?\{([^{}]+)\}/g,(function(e,n){return"\\"==e.charAt(0)?e.slice(1):null!=t[n]?t[n]:""}))}}),Function.extend({attempt:function(){for(var t=0,e=arguments.length;t<e;t++)try{return arguments[t]()}catch(t){}return null}}),Function.implement({attempt:function(t,e){try{return this.apply(e,Array.from(t))}catch(t){}return null},bind:function(t){var e=this,n=arguments.length>1?Array.slice(arguments,1):null,r=function(){},i=function(){var a=t,o=arguments.length;this instanceof i&&(r.prototype=e.prototype,a=new r);var s=n||o?e.apply(a,n&&o?n.concat(Array.slice(arguments)):n||arguments):e.call(a);return a==t?s:a};return i},pass:function(t,e){var n=this;return null!=t&&(t=Array.from(t)),function(){return n.apply(e,t||arguments)}},delay:function(t,e,n){return setTimeout(this.pass(null==n?[]:n,e),t)},periodical:function(t,e,n){return setInterval(this.pass(null==n?[]:n,e),t)}}),Number.implement({limit:function(t,e){return Math.min(e,Math.max(t,this))},round:function(t){return t=Math.pow(10,t||0).toFixed(t<0?-t:0),Math.round(this*t)/t},times:function(t,e){for(var n=0;n<this;n++)t.call(e,n,this)},toFloat:function(){return parseFloat(this)},toInt:function(t){return parseInt(this,t||10)}}),Number.alias("each","times"),function(t){var e={};["abs","acos","asin","atan","atan2","ceil","cos","exp","floor","log","max","min","pow","sin","sqrt","tan"].each((function(t){Number[t]||(e[t]=function(){return Math[t].apply(null,[this].concat(Array.from(arguments)))})})),Number.implement(e)}(),function(){var t=this.Class=new Type("Class",(function(r){instanceOf(r,Function)&&(r={initialize:r});var i=function(){if(n(this),i.$prototyping)return this;this.$caller=null;var t=this.initialize?this.initialize.apply(this,arguments):this;return this.$caller=this.caller=null,t}.extend(this).implement(r);return i.$constructor=t,i.prototype.$constructor=i,i.prototype.parent=e,i})),e=function(){if(!this.$caller)throw new Error('The method "parent" cannot be called.');var t=this.$caller.$name,e=this.$caller.$owner.parent,n=e?e.prototype[t]:null;if(!n)throw new Error('The method "'+t+'" has no parent.');return n.apply(this,arguments)},n=function(t){for(var e in t){var r=t[e];switch(typeOf(r)){case"object":var i=function(){};i.prototype=r,t[e]=n(new i);break;case"array":t[e]=r.clone()}}return t},r=function(e,n,r){if(t.Mutators.hasOwnProperty(e)&&null==(n=t.Mutators[e].call(this,n)))return this;if("function"==typeOf(n)){if(n.$hidden)return this;this.prototype[e]=r?n:function(t,e,n){n.$origin&&(n=n.$origin);var r=function(){if(n.$protected&&null==this.$caller)throw new Error('The method "'+e+'" cannot be called.');var t=this.caller,i=this.$caller;this.caller=i,this.$caller=r;var a=n.apply(this,arguments);return this.$caller=i,this.caller=t,a}.extend({$owner:t,$origin:n,$name:e});return r}(this,e,n)}else Object.merge(this.prototype,e,n);return this};t.implement("implement",r.overloadSetter()),t.Mutators={Extends:function(t){this.parent=t,this.prototype=function(t){t.$prototyping=!0;var e=new t;return delete t.$prototyping,e}(t)},Implements:function(t){Array.from(t).each((function(t){var e=new t;for(var n in e)r.call(this,n,e[n],!0)}),this)}}}(),function(){this.Chain=new Class({$chain:[],chain:function(){return this.$chain.append(Array.flatten(arguments)),this},callChain:function(){return!!this.$chain.length&&this.$chain.shift().apply(this,arguments)},clearChain:function(){return this.$chain.empty(),this}});var t=function(t){return t.replace(/^on([A-Z])/,(function(t,e){return e.toLowerCase()}))};this.Events=new Class({$events:{},addEvent:function(e,n,r){return e=t(e),this.$events[e]=(this.$events[e]||[]).include(n),r&&(n.internal=!0),this},addEvents:function(t){for(var e in t)this.addEvent(e,t[e]);return this},fireEvent:function(e,n,r){e=t(e);var i=this.$events[e];return i?(n=Array.from(n),i.each((function(t){r?t.delay(r,this,n):t.apply(this,n)}),this),this):this},removeEvent:function(e,n){e=t(e);var r=this.$events[e];if(r&&!n.internal){var i=r.indexOf(n);-1!=i&&delete r[i]}return this},removeEvents:function(e){var n;if("object"==typeOf(e)){for(n in e)this.removeEvent(n,e[n]);return this}for(n in e&&(e=t(e)),this.$events)if(!e||e==n)for(var r=this.$events[n],i=r.length;i--;)i in r&&this.removeEvent(n,r[i]);return this}}),this.Options=new Class({setOptions:function(){var t=this.options=Object.merge.apply(null,[{},this.options].append(arguments));if(this.addEvent)for(var e in t)"function"==typeOf(t[e])&&/^on[A-Z]/.test(e)&&(this.addEvent(e,t[e]),delete t[e]);return this}})}(),function(){var t=Object.prototype.hasOwnProperty;Object.extend({subset:function(t,e){for(var n={},r=0,i=e.length;r<i;r++){var a=e[r];a in t&&(n[a]=t[a])}return n},map:function(e,n,r){var i={};for(var a in e)t.call(e,a)&&(i[a]=n.call(r,e[a],a,e));return i},filter:function(e,n,r){var i={};for(var a in e){var o=e[a];t.call(e,a)&&n.call(r,o,a,e)&&(i[a]=o)}return i},every:function(e,n,r){for(var i in e)if(t.call(e,i)&&!n.call(r,e[i],i))return!1;return!0},some:function(e,n,r){for(var i in e)if(t.call(e,i)&&n.call(r,e[i],i))return!0;return!1},keys:function(e){var n=[];for(var r in e)t.call(e,r)&&n.push(r);return n},values:function(e){var n=[];for(var r in e)t.call(e,r)&&n.push(e[r]);return n},getLength:function(t){return Object.keys(t).length},keyOf:function(e,n){for(var r in e)if(t.call(e,r)&&e[r]===n)return r;return null},contains:function(t,e){return null!=Object.keyOf(t,e)},toQueryString:function(t,e){var n=[];return Object.each(t,(function(t,r){var i;switch(e&&(r=e+"["+r+"]"),typeOf(t)){case"object":i=Object.toQueryString(t,r);break;case"array":var a={};t.each((function(t,e){a[e]=t})),i=Object.toQueryString(a,r);break;default:i=r+"="+encodeURIComponent(t)}null!=t&&n.push(i)})),n.join("&")}})}(),"undefined"!=typeof exports&&function(){for(var t in this)GLOBAL_ITEMS.contains(t)||(exports[t]=this[t]);exports.apply=function(t){Object.append(t,exports)}}(),MooTools.More={version:"1.6.1-dev",build:"%build%"},function(){var t=function(t){return null!=t},e=Object.prototype.hasOwnProperty;Object.extend({getFromPath:function(t,n){"string"==typeof n&&(n=n.split("."));for(var r=0,i=n.length;r<i;r++){if(!e.call(t,n[r]))return null;t=t[n[r]]}return t},cleanValues:function(e,n){for(var r in n=n||t,e)n(e[r])||delete e[r];return e},erase:function(t,n){return e.call(t,n)&&delete t[n],t},run:function(t){var e=Array.slice(arguments,1);for(var n in t)t[n].apply&&t[n].apply(t,e);return t}})}(),function(){var t=null,e={},n=function(t){return instanceOf(t,r.Set)?t:e[t]},r=this.Locale={define:function(n,i,a,o){var s;return instanceOf(n,r.Set)?(s=n.name)&&(e[s]=n):(e[s=n]||(e[s]=new r.Set(s)),n=e[s]),i&&n.define(i,a,o),t||(t=n),n},use:function(e){return(e=n(e))&&(t=e,this.fireEvent("change",e)),this},getCurrent:function(){return t},get:function(e,n){return t?t.get(e,n):""},inherit:function(t,e,r){return(t=n(t))&&t.inherit(e,r),this},list:function(){return Object.keys(e)}};Object.append(r,new Events),r.Set=new Class({sets:{},inherits:{locales:[],sets:{}},initialize:function(t){this.name=t||""},define:function(t,e,n){var r=this.sets[t];return r||(r={}),e&&("object"==typeOf(e)?r=Object.merge(r,e):r[e]=n),this.sets[t]=r,this},get:function(t,n,r){var i=Object.getFromPath(this.sets,t);if(null!=i){var a=typeOf(i);return"function"==a?i=i.apply(null,Array.convert(n)):"object"==a&&(i=Object.clone(i)),i}var o=t.indexOf("."),s=o<0?t:t.substr(0,o),u=(this.inherits.sets[s]||[]).combine(this.inherits.locales).include("en-US");r||(r=[]);for(var c=0,l=u.length;c<l;c++)if(!r.contains(u[c])){r.include(u[c]);var h=e[u[c]];if(h&&null!=(i=h.get(t,n,r)))return i}return""},inherit:function(t,e){t=Array.convert(t),e&&!this.inherits.sets[e]&&(this.inherits.sets[e]=[]);for(var n=t.length;n--;)(e?this.inherits.sets[e]:this.inherits.locales).unshift(t[n]);return this}})}(),Locale.define("en-US","Date",{months:["January","February","March","April","May","June","July","August","September","October","November","December"],months_abbr:["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"],days:["Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"],days_abbr:["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],dateOrder:["month","date","year"],shortDate:"%m/%d/%Y",shortTime:"%I:%M%p",AM:"AM",PM:"PM",firstDayOfWeek:0,ordinal:function(t){return t>3&&t<21?"th":["th","st","nd","rd","th"][Math.min(t%10,4)]},lessThanMinuteAgo:"less than a minute ago",minuteAgo:"about a minute ago",minutesAgo:"{delta} minutes ago",hourAgo:"about an hour ago",hoursAgo:"about {delta} hours ago",dayAgo:"1 day ago",daysAgo:"{delta} days ago",weekAgo:"1 week ago",weeksAgo:"{delta} weeks ago",monthAgo:"1 month ago",monthsAgo:"{delta} months ago",yearAgo:"1 year ago",yearsAgo:"{delta} years ago",lessThanMinuteUntil:"less than a minute from now",minuteUntil:"about a minute from now",minutesUntil:"{delta} minutes from now",hourUntil:"about an hour from now",hoursUntil:"about {delta} hours from now",dayUntil:"1 day from now",daysUntil:"{delta} days from now",weekUntil:"1 week from now",weeksUntil:"{delta} weeks from now",monthUntil:"1 month from now",monthsUntil:"{delta} months from now",yearUntil:"1 year from now",yearsUntil:"{delta} years from now"}),function(){var t=this.Date,e=t.Methods={ms:"Milliseconds",year:"FullYear",min:"Minutes",mo:"Month",sec:"Seconds",hr:"Hours"};["Date","Day","FullYear","Hours","Milliseconds","Minutes","Month","Seconds","Time","TimezoneOffset","Week","Timezone","GMTOffset","DayOfYear","LastMonth","LastDayOfMonth","UTCDate","UTCDay","UTCFullYear","AMPM","Ordinal","UTCHours","UTCMilliseconds","UTCMinutes","UTCMonth","UTCSeconds","UTCMilliseconds"].each((function(e){t.Methods[e.toLowerCase()]=e}));var n=function(t,e,r){return 1==e?t:t<Math.pow(10,e-1)?(r||"0")+n(t,e-1,r):t};t.implement({set:function(t,n){t=t.toLowerCase();var r=e[t]&&"set"+e[t];return r&&this[r]&&this[r](n),this}.overloadSetter(),get:function(t){t=t.toLowerCase();var n=e[t]&&"get"+e[t];return n&&this[n]?this[n]():null}.overloadGetter(),clone:function(){return new t(this.get("time"))},increment:function(e,n){switch(n=null!=n?n:1,e=e||"day"){case"year":return this.increment("month",12*n);case"month":var r=this.get("date");return this.set("date",1).set("mo",this.get("mo")+n),this.set("date",r.min(this.get("lastdayofmonth")));case"week":return this.increment("day",7*n);case"day":return this.set("date",this.get("date")+n)}if(!t.units[e])throw new Error(e+" is not a supported interval");return this.set("time",this.get("time")+n*t.units[e]())},decrement:function(t,e){return this.increment(t,-1*(null!=e?e:1))},isLeapYear:function(){return t.isLeapYear(this.get("year"))},clearTime:function(){return this.set({hr:0,min:0,sec:0,ms:0})},diff:function(e,n){return"string"==typeOf(e)&&(e=t.parse(e)),((e-this)/t.units[n||"day"](3,3)).round()},getLastDayOfMonth:function(){return t.daysInMonth(this.get("mo"),this.get("year"))},getDayOfYear:function(){return(t.UTC(this.get("year"),this.get("mo"),this.get("date")+1)-t.UTC(this.get("year"),0,1))/t.units.day()},setDay:function(e,n){null==n&&""===(n=t.getMsg("firstDayOfWeek"))&&(n=1),e=(7+t.parseDay(e,!0)-n)%7;var r=(7+this.get("day")-n)%7;return this.increment("day",e-r)},getWeek:function(e){null==e&&""===(e=t.getMsg("firstDayOfWeek"))&&(e=1);var n,r=this,i=(7+r.get("day")-e)%7,a=0;if(1==e){var o=r.get("month"),s=r.get("date")-i;if(11==o&&s>28)return 1;0==o&&s<-2&&(r=new t(r).decrement("day",i),i=0),(n=new t(r.get("year"),0,1).get("day")||7)>4&&(a=-7)}else n=new t(r.get("year"),0,1).get("day");return a+=r.get("dayofyear"),a+=6-i,(a+=(7+n-e)%7)/7},getOrdinal:function(e){return t.getMsg("ordinal",e||this.get("date"))},getTimezone:function(){return this.toString().replace(/^.*? ([A-Z]{3}).[0-9]{4}.*$/,"$1").replace(/^.*?\(([A-Z])[a-z]+ ([A-Z])[a-z]+ ([A-Z])[a-z]+\)$/,"$1$2$3")},getGMTOffset:function(){var t=this.get("timezoneOffset");return(t>0?"-":"+")+n((t.abs()/60).floor(),2)+n(t%60,2)},setAMPM:function(t){t=t.toUpperCase();var e=this.get("hr");return e>11&&"AM"==t?this.decrement("hour",12):e<12&&"PM"==t?this.increment("hour",12):this},getAMPM:function(){return this.get("hr")<12?"AM":"PM"},parse:function(e){return this.set("time",t.parse(e)),this},isValid:function(t){return t||(t=this),"date"==typeOf(t)&&!isNaN(t.valueOf())},format:function(e){if(!this.isValid())return"invalid date";if(e||(e="%x %X"),"string"==typeof e&&(e=a[e.toLowerCase()]||e),"function"==typeof e)return e(this);var r=this;return e.replace(/%([a-z%])/gi,(function(e,i){switch(i){case"a":return t.getMsg("days_abbr")[r.get("day")];case"A":return t.getMsg("days")[r.get("day")];case"b":return t.getMsg("months_abbr")[r.get("month")];case"B":return t.getMsg("months")[r.get("month")];case"c":return r.format("%a %b %d %H:%M:%S %Y");case"d":return n(r.get("date"),2);case"e":return n(r.get("date"),2," ");case"H":return n(r.get("hr"),2);case"I":return n(r.get("hr")%12||12,2);case"j":return n(r.get("dayofyear"),3);case"k":return n(r.get("hr"),2," ");case"l":return n(r.get("hr")%12||12,2," ");case"L":return n(r.get("ms"),3);case"m":return n(r.get("mo")+1,2);case"M":return n(r.get("min"),2);case"o":return r.get("ordinal");case"p":return t.getMsg(r.get("ampm"));case"s":return Math.round(r/1e3);case"S":return n(r.get("seconds"),2);case"T":return r.format("%H:%M:%S");case"U":return n(r.get("week"),2);case"w":return r.get("day");case"x":return r.format(t.getMsg("shortDate"));case"X":return r.format(t.getMsg("shortTime"));case"y":return r.get("year").toString().substr(2);case"Y":return r.get("year");case"z":return r.get("GMTOffset");case"Z":return r.get("Timezone")}return i}))},toISOString:function(){return this.format("iso8601")}}).alias({toJSON:"toISOString",compare:"diff",strftime:"format"});var r=["Sun","Mon","Tue","Wed","Thu","Fri","Sat"],i=["Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"],a={db:"%Y-%m-%d %H:%M:%S",compact:"%Y%m%dT%H%M%S",short:"%d %b %H:%M",long:"%B %d, %Y %H:%M",rfc822:function(t){return r[t.get("day")]+t.format(", %d ")+i[t.get("month")]+t.format(" %Y %H:%M:%S %Z")},rfc2822:function(t){return r[t.get("day")]+t.format(", %d ")+i[t.get("month")]+t.format(" %Y %H:%M:%S %z")},iso8601:function(t){return t.getUTCFullYear()+"-"+n(t.getUTCMonth()+1,2)+"-"+n(t.getUTCDate(),2)+"T"+n(t.getUTCHours(),2)+":"+n(t.getUTCMinutes(),2)+":"+n(t.getUTCSeconds(),2)+"."+n(t.getUTCMilliseconds(),3)+"Z"}},o=[],s=t.parse,u=function(e,n,r){var i=-1,a=t.getMsg(e+"s");switch(typeOf(n)){case"object":i=a[n.get(e)];break;case"number":if(!(i=a[n]))throw new Error("Invalid "+e+" index: "+n);break;case"string":var o=a.filter((function(t){return this.test(t)}),new RegExp("^"+n,"i"));if(!o.length)throw new Error("Invalid "+e+" string");if(o.length>1)throw new Error("Ambiguous "+e);i=o[0]}return r?a.indexOf(i):i},c=1900,l=70;t.extend({getMsg:function(t,e){return Locale.get("Date."+t,e)},units:{ms:Function.convert(1),second:Function.convert(1e3),minute:Function.convert(6e4),hour:Function.convert(36e5),day:Function.convert(864e5),week:Function.convert(6084e5),month:function(e,n){var r=new t;return 864e5*t.daysInMonth(null!=e?e:r.get("mo"),null!=n?n:r.get("year"))},year:function(e){return e=e||(new t).get("year"),t.isLeapYear(e)?316224e5:31536e6}},daysInMonth:function(e,n){return[31,t.isLeapYear(n)?29:28,31,30,31,30,31,31,30,31,30,31][e]},isLeapYear:function(t){return t%4==0&&t%100!=0||t%400==0},parse:function(e){var n,r=typeOf(e);return"number"==r?new t(e):"string"!=r?e:(e=e.clean()).length?(o.some((function(t){var r=t.re.exec(e);return!!r&&(n=t.handler(r))})),n&&n.isValid()||(n=new t(s(e)))&&n.isValid()||(n=new t(e.toInt())),n):null},parseDay:function(t,e){return u("day",t,e)},parseMonth:function(t,e){return u("month",t,e)},parseUTC:function(e){var n=new t(e),r=t.UTC(n.get("year"),n.get("mo"),n.get("date"),n.get("hr"),n.get("min"),n.get("sec"),n.get("ms"));return new t(r)},orderIndex:function(e){return t.getMsg("dateOrder").indexOf(e)+1},defineFormat:function(t,e){return a[t]=e,this},defineParser:function(t){return o.push(t.re&&t.handler?t:g(t)),this},defineParsers:function(){return Array.flatten(arguments).each(t.defineParser),this},define2DigitYearStart:function(t){return c=t-(l=t%100),this}}).extend({defineFormats:t.defineFormat.overloadSetter()});var h,f=function(e){return new RegExp("(?:"+t.getMsg(e).map((function(t){return t.substr(0,3)})).join("|")+")[a-z]*")},d={d:/[0-2]?[0-9]|3[01]/,H:/[01]?[0-9]|2[0-3]/,I:/0?[1-9]|1[0-2]/,M:/[0-5]?\d/,s:/\d+/,o:/[a-z]*/,p:/[ap]\.?m\.?/,y:/\d{2}|\d{4}/,Y:/\d{4}/,z:/Z|[+-]\d{2}(?::?\d{2})?/};d.m=d.I,d.S=d.M;var g=function(e){if(!h)return{format:e};var n=[],r=(e.source||e).replace(/%([a-z])/gi,(function(e,n){return function(e){switch(e){case"T":return"%H:%M:%S";case"x":return(1==t.orderIndex("month")?"%m[-./]%d":"%d[-./]%m")+"([-./]%y)?";case"X":return"%H([.:]%M)?([.:]%S([.:]%s)?)? ?%p? ?%z?"}return null}(n)||e})).replace(/\((?!\?)/g,"(?:").replace(/ (?!\?|\*)/g,",? ").replace(/%([a-z%])/gi,(function(t,e){var r=d[e];return r?(n.push(e),"("+r.source+")"):e})).replace(/\[a-z\]/gi,"[a-z\\u00c0-\\uffff;&]");return{format:e,re:new RegExp("^"+r+"$","i"),handler:function(e){e=e.slice(1).associate(n);var r=(new t).clearTime(),i=e.y||e.Y;for(var a in null!=i&&p.call(r,"y",i),"d"in e&&p.call(r,"d",1),("m"in e||e.b||e.B)&&p.call(r,"m",1),e)p.call(r,a,e[a]);return r}}},p=function(e,n){if(!n)return this;switch(e){case"a":case"A":return this.set("day",t.parseDay(n,!0));case"b":case"B":return this.set("mo",t.parseMonth(n,!0));case"d":return this.set("date",n);case"H":case"I":return this.set("hr",n);case"m":return this.set("mo",n-1);case"M":return this.set("min",n);case"p":return this.set("ampm",n.replace(/\./g,""));case"S":return this.set("sec",n);case"s":return this.set("ms",1e3*("0."+n));case"w":return this.set("day",n);case"Y":return this.set("year",n);case"y":return(n=+n)<100&&(n+=c+(n<l?100:0)),this.set("year",n);case"z":"Z"==n&&(n="+00");var r=n.match(/([+-])(\d{2}):?(\d{2})?/);return r=(r[1]+"1")*(60*r[2]+(+r[3]||0))+this.getTimezoneOffset(),this.set("time",this-6e4*r)}return this};t.defineParsers("%Y([-./]%m([-./]%d((T| )%X)?)?)?","%Y%m%d(T%H(%M%S?)?)?","%x( %X)?","%d%o( %b( %Y)?)?( %X)?","%b( %d%o)?( %Y)?( %X)?","%Y %b( %d%o( %X)?)?","%o %b %d %X %z %Y","%T","%H:%M( ?%p)?"),Locale.addEvent("change",(function(t){Locale.get("Date")&&function(t){h=t,d.a=d.A=f("days"),d.b=d.B=f("months"),o.each((function(t,e){t.format&&(o[e]=g(t.format))}))}(t)})).fireEvent("change",Locale.getCurrent())}(),Locale.define("zh-CHS","Date",{months:["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"],months_abbr:["一","二","三","四","五","六","七","八","九","十","十一","十二"],days:["星期日","星期一","星期二","星期三","星期四","星期五","星期六"],days_abbr:["日","一","二","三","四","五","六"],dateOrder:["year","month","date"],shortDate:"%Y-%m-%d",shortTime:"%I:%M%p",AM:"AM",PM:"PM",firstDayOfWeek:1,ordinal:"",lessThanMinuteAgo:"不到1分钟前",minuteAgo:"大约1分钟前",minutesAgo:"{delta}分钟之前",hourAgo:"大约1小时前",hoursAgo:"大约{delta}小时前",dayAgo:"1天前",daysAgo:"{delta}天前",weekAgo:"1星期前",weeksAgo:"{delta}星期前",monthAgo:"1个月前",monthsAgo:"{delta}个月前",yearAgo:"1年前",yearsAgo:"{delta}年前",lessThanMinuteUntil:"从现在开始不到1分钟",minuteUntil:"从现在开始約1分钟",minutesUntil:"从现在开始约{delta}分钟",hourUntil:"从现在开始1小时",hoursUntil:"从现在开始约{delta}小时",dayUntil:"从现在开始1天",daysUntil:"从现在开始{delta}天",weekUntil:"从现在开始1星期",weeksUntil:"从现在开始{delta}星期",monthUntil:"从现在开始一个月",monthsUntil:"从现在开始{delta}个月",yearUntil:"从现在开始1年",yearsUntil:"从现在开始{delta}年"}),Locale.define("zh-CHT","Date",{months:["一月","二月","三月","四月","五月","六月","七月","八月","九月","十月","十一月","十二月"],months_abbr:["一","二","三","四","五","六","七","八","九","十","十一","十二"],days:["星期日","星期一","星期二","星期三","星期四","星期五","星期六"],days_abbr:["日","一","二","三","四","五","六"],dateOrder:["year","month","date"],shortDate:"%Y-%m-%d",shortTime:"%I:%M%p",AM:"AM",PM:"PM",firstDayOfWeek:1,ordinal:"",lessThanMinuteAgo:"不到1分鐘前",minuteAgo:"大約1分鐘前",minutesAgo:"{delta}分鐘之前",hourAgo:"大約1小時前",hoursAgo:"大約{delta}小時前",dayAgo:"1天前",daysAgo:"{delta}天前",weekAgo:"1星期前",weeksAgo:"{delta}星期前",monthAgo:"1个月前",monthsAgo:"{delta}个月前",yearAgo:"1年前",yearsAgo:"{delta}年前",lessThanMinuteUntil:"從現在開始不到1分鐘",minuteUntil:"從現在開始約1分鐘",minutesUntil:"從現在開始約{delta}分鐘",hourUntil:"從現在開始1小時",hoursUntil:"從現在開始約{delta}小時",dayUntil:"從現在開始1天",daysUntil:"從現在開始{delta}天",weekUntil:"從現在開始1星期",weeksUntil:"從現在開始{delta}星期",monthUntil:"從現在開始一個月",monthsUntil:"從現在開始{delta}個月",yearUntil:"從現在開始1年",yearsUntil:"從現在開始{delta}年"}),"undefined"==typeof JSON&&(this.JSON={}),function(){var special={"\b":"\\b","\t":"\\t","\n":"\\n","\f":"\\f","\r":"\\r",'"':'\\"',"\\":"\\\\"},escape=function(t){return special[t]||"\\u"+("0000"+t.charCodeAt(0).toString(16)).slice(-4)};JSON.validate=function(t){return t=t.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,"@").replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,"]").replace(/(?:^|:|,)(?:\s*\[)+/g,""),/^[\],:{}\s]*$/.test(t)},JSON.encode=JSON.stringify?function(t){return JSON.stringify(t)}:function(t){switch(t&&t.toJSON&&(t=t.toJSON()),typeOf(t)){case"string":return'"'+t.replace(/[\x00-\x1f\\"]/g,escape)+'"';case"array":return"["+t.map(JSON.encode).clean()+"]";case"object":case"hash":var e=[];return Object.each(t,(function(t,n){var r=JSON.encode(t);r&&e.push(JSON.encode(n)+":"+r)})),"{"+e+"}";case"number":case"boolean":return""+t;case"null":return"null"}return null},JSON.decode=function(string,secure){if(!string||"string"!=typeOf(string))return null;if(secure||JSON.secure){if(JSON.parse)return JSON.parse(string);if(!JSON.validate(string))throw new Error("JSON could not decode the input; security is enabled and the value is not secure.")}return eval("("+string+")")}}();

//fetch 메서드
const _getBytes = function(str){
    const buffer = new ArrayBuffer(str.length);
    const uint8Array = new Uint8Array(buffer);
    for (let i = 0; i < str.length; i++) {
        uint8Array[i] = str.charCodeAt(i);
    }
    return uint8Array;
}
/**
 * this.fetch는 http 요청을 보내는 메서드.<br/>
 * fetch 메서드는 브라우저 환경의 Fetch API를 간소화해 모방함. fetch 메서드는 java.net.HttpURLConnection 클래스를 호출해 요청을 보냄.
 * @module fetch()
 * @o2category server.common
 * @o2ordernumber 150
 * @o2cn 메서드 정의
 * @param {(String)} url 리소스를 가져올 URL.
 * @param {Object} options  요청 설정을 포함하는 설정 객체. 선택 매개변수: method, headers, body
 * @return {Promise} resolve 시 Response 객체 전달.
 * @o2syntax
 * const resPromise = fetch(url, options);
 * @example
 * <caption>
 *    <b>예시 1:<b> GET 요청을 보내 응답 텍스트 조회:
 * </caption>
 * fetch('http://hostname/resource').then((res)=>{
 *      return res.text();
 * }).then((text)=>{
 *      console.log(`응답 텍스트 정보: ${text}`);
 * });
 * @example
 * <caption>
 *    <b>예시 21:<b> get 요청 실행. json 데이터를 조회하고 header에 인증 정보 첨부.
 * </caption>
 * fetch('http://hostname/resource', {
 *     headers: {
 *         "Authorization": "xxxxxxxxxxxxx"
 *     }
 * }).then((res)=>{
 *      return res.json();
 * }).then((json)=>{
 *      console.log(`응답 json 객체:${JSON.stringify(json, null, '\t')}`);
 * });
 *
 * @example
 * <caption>
 *    <b>예시 3:<b> POST 요청을 실행하고 응답 json 조회:
 * </caption>
 * fetch('http://hostname/resource', {
 *     method: 'post',
 *     body: '{"postData": "this is post data"}',
 *     headers: {
 *         "Content-Type": "application/json; charset=UTF-8"
 *     }
 * }).then((res)=>{
 *      return res.json();
 * }).then((json)=>{
 *      console.log(`응답 json 객체:${JSON.stringify(json, null, '\t')}`);
 * });
 *
 * @example
 * <caption>
 *    <b>예시 4:<b> res.blob() 메서드로 Response 스트림(java.io.ByteArrayInputStream) 조회, 또는 res.arrayBuffer() 메서드로 Response의 ArrayBuffer 객체 조회
 * </caption>
 * fetch('http://hostname/resource').then((res)=>{
 *      return res.blob();
 * }).then((blob)=>{
 *      //조회한 blob, 실제로는 (java.io.ByteArrayInputStream) 객체
 * });
 *
 * fetch('http://hostname/resource').then((res)=>{
 *      return res.arrayBuffer();
 * }).then((arrayBuffer)=>{
 *      //조회한 arrayBuffer 객체
 * });
 *
 * @example
 * <caption>
 *    <b>예시 5:<b> 오류 처리. 네트워크 오류가 발생하면 fetch는 reject되지만, 500, 415 등 서버 응답 오류는 네트워크 장애가 아니므로 fetch() 성공 여부를 정확히 판단하려면 promise resolved 인 경우를 포함해 res.ok 가 true 인지 확인해야 함.
 * </caption>
 * fetch('http://hostname/resource').then((res)=>{
 *      if (!res.ok){
 *          throw new Error("response was not OK");
 *      }
 *      return res.json();
 * }).then((json)=>{
 *      console.log(`응답 json 객체:${JSON.stringify(json, null, '\t')}`);
 * }).catch((e)=>{
 *     //요청 오류 처리
 * });
 *
 * @example
 * <caption>
 *    <b>예시 6:<b> 응답 정보 조회.
 * </caption>
 * fetch('http://hostname/resource').then((res)=>{
 *      res.ok          //요청 성공 여부
 *      res.status      //요청 응답 상태 코드
 *      res.statusText  //요청 응답 상태 텍스트
 *      res.headers     //응답 헤더
 *      res.error       //java.net.HttpURLConnection의 오류 스트림
 *      res.connection  //java.net.HttpURLConnection 객체
 *
 *      res.text()          //응답 내용 텍스트
 *      res.json()          //응답 내용 json 객체
 *      해당 사용자의 속성 값 배열 반환.
 *      res.arrayBuffer()   //응답 내용 Response의 ArrayBuffer 객체
 * });
 *
 * @example
 * <caption>
 *    <b>예시 7:<b> await 사용.
 * </caption>
 * try{
 *      const res = await fetch('http://hostname/resource');
 *      if (res.ok){
 *          const json = await res.json();
 *          //json 객체 조회
 *      }else{
 *          log.error(`요청 오류 발생: ${res.statusText}`);
 *      }
 * }catch(e){
 *      log.error(`요청 오류 발생: ${e.message}`);
 * }
 */
const fetch = function(url, options = {}) {
    const JavaURL = Java.type('java.net.URL');
    const urlObj = new JavaURL(url);
    return new Promise((resolve, reject) => {
        try{
            const httpURLConnection = urlObj.openConnection();
            // 요청 메서드 설정
            httpURLConnection.setRequestMethod(options.method ? options.method.toUpperCase() : 'GET');
            // 요청 헤더 설정
            if (options.headers) {
                for (const [header, value] of Object.entries(options.headers)) {
                    httpURLConnection.setRequestProperty(header, value);
                }
            }

            // 요청 본문 처리
            if (options.body) {
                httpURLConnection.setDoOutput(true);
                const output = httpURLConnection.getOutputStream();
                output.write(_getBytes(options.body));
                output.flush();
                output.close();
            }

            // 응답 코드 조회
            const responseCode = httpURLConnection.getResponseCode();

            const getResponseBody = function(inputStream, encode){
                const JavaInputStreamReader = Java.type('java.io.InputStreamReader');
                const JavaBufferedReader = Java.type('java.io.BufferedReader');
                const reader = new JavaBufferedReader(new JavaInputStreamReader(inputStream, encode));
                let inputLine;
                const responseData = [];
                while ((inputLine = reader.readLine()) !== null) {
                    responseData.push(inputLine);
                }
                reader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return responseData.join('\n');
            }

            if (responseCode >= 200 && responseCode < 300) {};
            const isOk = (responseCode >= 200 && responseCode < 300);
            const inputStream = (isOk) ? httpURLConnection.getInputStream() : httpURLConnection.getErrorStream();

            const responseObject = {
                ok: isOk,
                status: responseCode,
                statusText: httpURLConnection.getResponseMessage(),
                headers: httpURLConnection.getHeaderFields(),
                error: httpURLConnection.getErrorStream(),

                text: async (encode = 'utf-8') => getResponseBody(inputStream, encode),
                json: async (encode = 'utf-8') => JSON.parse(getResponseBody(inputStream, encode)),

                blob: async () => {
                    const byteArray = inputStream.readAllBytes();
                    const JavaByteArrayInputStream = Java.type('java.io.ByteArrayInputStream');
                    return new JavaByteArrayInputStream(byteArray);
                },

                arrayBuffer: async () => {
                    const byteArray = inputStream.readAllBytes();
                    return Uint8Array.from(byteArray).buffer;
                },

                body: {
                    getReader: () => ({
                        read: () => {
                            const value = inputStream.read();
                            return { value, done: value === -1 };
                        },
                    }),
                    cancel: () => {
                        httpURLConnection.disconnect();
                    },
                    // add other body related properties/methods as needed
                },
                connection: httpURLConnection
            };
            resolve(responseObject);
        }catch (e){
            reject(e);
        }
    });
}

//JS 스크립트 실행
/**
 * 코드 조각 실행, eval과 유사. Function으로 감싸서 실행.
 * @module exec()
 * @o2category server.common
 * @o2ordernumber 151
 * @o2cn 메서드 정의
 * @param text {String} 실행할 코드 조각
 * @param bind {Object} 코드 조각에서 this가 가리키는 대상
 * @param arg {Object} 전달 인자. 예: {x:1, y:'5'}, 코드 조각에서 x와 y 변수를 사용할 수 있음.
 * @param throwError {boolean} 코드 조각 실행 오류 시 오류를 던질지 여부
 * @returns {any} 실행한 코드 조각의 반환값
 */
const exec = function(text, bind, arg=null, throwError=true){
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

//메서드 정의
/**
 * this.define은 메서드이며, 스크립트에서 this.define()으로 자체 메서드를 정의할 수 있음.<br/>
 * 이 방식으로 메서드를 정의하면 서로 다른 앱에서 같은 메서드 이름을 사용해도 충돌하지 않습니다.
 * @module define()
 * @o2category server.common
 * @o2ordernumber 160
 * @o2cn 메서드 정의
 * @param {(String)} name 정의할 메서드 이름.
 * @param {Function} fun  정의할 메서드
 * @param {Boolean} [overwrite] 정의한 메서드의 덮어쓰기 허용 여부. 기본값 true.
 * @o2syntax
 * this.define(name, fun, overwrite)
 * @example
 * <caption>
 *    <b>예시 1:</b> 공용 스크립트에서 공문 관리의 모든 문서 종류를 조회하는 공용 메서드를 정의하고, 쿼리 구문에서 이 메서드로 JPQL을 조합.<br/>
 *     1. 콘텐츠 관리에 fileRes 앱이 있고, 이 앱에서 스크립트를 만들어 FileSql로 이름 짓고 메서드를 정의.
 *     <img src='img/module/include/server_define1.png' />
 * </caption>
 * //메서드 정의
 * this.define("getFileSQL",function(){
 *   const application = ["회사 발신 문서","부서 발신 문서","당위원회 발신 문서"];
 *   const appSql = " ( ";
 *   for (let i=0; i<application.length; i++){
 *       if (i==application.length-1){
 *           appSql = appSql + " o.applicationName = '"+application[i]+"' "
 *       }else{
 *           appSql = appSql + " o.applicationName = '"+application[i]+"' OR "
 *       }
 *   }
 *   appSql = appSql + " ) ";
 *   return appSql;
 *});
 * @example
 * <caption>
 *      2. 쿼리 구문에서 이 메서드 사용.
 *     <img src='img/module/include/server_define2.png'/>
 * </caption>
 * this.include({
 *   type : "cms",
 *   application : "fileRes",
 *   name : "FileSql"
 * })
 *
 * var sql = this.getFileSQL();
 *
 * return "SELECT o FROM com.x.processplatform.core.entity.content.Task o WHERE "+sql
 */
const define = function(name, fun, overwrite){
    const over = overwrite===false;
    const o = {};
    o[name] = {"value": fun, "configurable": over};
    Object.defineProperties(globalThis, o);
}

//restful 서비스 Action
const Action = function(root, json){
    this.actions = json;
    this.root = root;

    var invokeFunction = function(service, parameters, name){
        var _self = this;
        return function(...pars){
            let success, failure, async, data, file;
            const parameter = {};
            const firstType = typeof pars[0];

            if (firstType==='function'){
                success = pars.shift();
                failure = pars.shift();
            }

            parameters.each((p)=>{
                parameter[p] = pars.shift();
            });
            if (['post', 'put'].includes(service['method']?.toLowerCase())){
                data = pars.shift();
            }

            if (firstType!=='function'){
                success = pars.shift();
                failure = pars.shift();
            }
            const person = pars.shift();

            return _self.invoke({name, data, parameter, success, failure, person});
        };
    };
    var createMethod = function(service, key){
        const jaxrsUri = service.uri;
        const re = new RegExp("(?<={).+?(?=})", "g");
        const parameters = jaxrsUri.match(re) || [];
        this[key] = invokeFunction.call(this, service, parameters, key);
    };

    this.invoke = function(option){
        if (this.actions[option.name]){
            let uri = this.actions[option.name].uri;
            const method = this.actions[option.name].method || "get";
            if (option.parameter){
                Object.keys(option.parameter).forEach(function(key){
                    var v = option.parameter[key];
                    uri = uri.replace("{"+key+"}", v);
                });
            }
            let res = null;
            const person = option.person || null;

            try{
                switch (method.toLowerCase()){
                    case "get":
                        res = globalThis.applications[!!person ? 'authorizedGetQuery' : 'getQuery'](this.root, uri, person);
                        break;
                    case "post":
                        res = globalThis.applications[!!person ? 'authorizedPostQuery' : 'postQuery'](this.root, uri, JSON.stringify(option.data), person);
                        break;
                    case "put":
                        res = globalThis.applications[!!person ? 'authorizedPutQuery' : 'putQuery'](this.root, uri, JSON.stringify(option.data), person);
                        break;
                    case "delete":
                        res = globalThis.applications[!!person ? 'authorizedDeleteQuery' : 'deleteQuery'](this.root, uri, person);
                        break;
                    default:
                        res = globalThis.applications[!!person ? 'authorizedGetQuery' : 'getQuery'](this.root, uri, person);
                }
                if (res && res.getType().toString()==="success"){
                    const json = JSON.parse(res.toString());
                    if (option.success) option.success(json);
                    return json;
                }else{
                    const json = ((res) ? JSON.parse(res.toString()) : null)
                    if (option.failure) option.failure(json);
                    return json;
                }
            }catch(e){
                if (option.failure) option.failure(e);
            }
            return res;
        }
    };

    Object.keys(this.actions).forEach((key)=>{
        const service = this.actions[key];
        if (service.uri) if (!this[key]) createMethod.call(this, service, key);
    });
}
Object.assign(Action, {
    get applications(){
        return (globalThis.java_resources) ? globalThis.java_resources.getApplications() : null;
    }
});

/**
 * 이 문서는 백엔드 스크립트에서 Actions로 플랫폼의 RESTful 서비스를 호출하는 방법을 설명함.<br/>
 * 다음 주소에서 서비스 목록 조회: http://server/x_program_center/jest/list.html (v7.2 이전 버전은 포트 20030 추가 필요)
 * @module server.Actions
 * @o2cn 서비스 호출
 * @o2category server.common
 * @o2ordernumber 165
 * @o2syntax
 * //Actions 조회
 * this.Actions
 */
const Actions = {
    "loadedActions": {},
    /**
     * 플랫폼은 플랫폼 제공 서비스를 호출하는 Actions 객체를 미리 제공하며, this.Actions.load로 이 메서드들을 가져올 수 있습니다. 서버에서 실행되므로 서비스는 모두 동기 호출입니다.
     * @method load
     * @methodOf module:server.Actions
     * @instance
     * @param {String} root 플랫폼 RESTful 서비스 루트, 서비스 목록은 http://server/x_program_center/jest/list.html 참고.(v7.2 이전 버전은 포트 20030 추가 필요)
     * 예:
     *<pre><code class='language-js'>
     * "x_processplatform_assemble_surface" //프로세스 플랫폼 관련 서비스 루트
     * </code></pre>
     * @return {Object} 이후 서비스 호출에 사용할 action 객체 반환
     * @o2syntax
     * var actions = this.Actions.load( root );
     * @o2syntax
     * //프로세스 플랫폼 서비스 객체 조회.
     * var processAction = this.Actions.load("x_processplatform_assemble_surface");
     * @o2syntax
     * <caption>
     *     this.Actions.load(root) 메서드로 action 객체를 얻으면 이 서비스의 메서드에 접근할 수 있습니다.<br/>
     *     메서드 접근 규칙은 다음과 같습니다:
     *  </caption>
     *  var requestString = this.Actions.load( root )[actionName][methodName]( arguements );
     *
     *  requestString : 서비스가 반환한 응답 데이터, 문자열 형식, requestObjest = JSON.parse(requestString); 로 객체로 파싱 가능
     *
     *  root : 플랫폼 서비스 루트 이름. 예: x_processplatform_assemble_surface
     *
     *  actionName : 서비스 아래 Action 분류 이름. 예: TaskAction
     *
     *  methodName : Action 분류 아래 메서드 이름. 예: get
     *
     *  arguements : 호출할 RESTful 서비스의 관련 매개변수. 순서대로 전달해야 하며, 상황에 따라 일부 매개변수는 생략할 수 있습니다. 매개변수 순서는 다음과 같습니다:
     *
     *      uri 파라미터, data(Post, Put 메서드), success, failure, async.
     *
     *      uri 매개변수: uri 매개변수가 여러 개이면 순서대로 전달해야 합니다.
     *
     *      data 파라미터: 백엔드에 제출할 데이터. POST 와 PUT 메서드는 전달 필요, GET 메서드와 DELETE 메서드는 생략.
     *
     *      success 매개변수: 서비스 실행 성공 시 콜백 메서드. 형식: function(json){
     *          json은 서버 서비스가 반환한 데이터
     *      }。
     *
     *      failure 매개변수: 서비스 실행 실패 시 콜백 메서드. 형식: function(xhr){
     *          xhr XmlHttpRequest 객체, 서버 요청 실패 시 값이 있음
     *       }
     *      이 매개변수는 생략할 수 있으며, 생략하면 시스템이 오류 메시지를 자동으로 띄웁니다.
     *  @o2syntax
     *  <caption>
     *  반환 데이터 처리 방법은 두 가지이며 하나를 선택하면 됩니다:<br/>
     *  1. 이 메서드가 반환하는 결과는 응답 json 객체.<br/>
     *  2. success 메서드를 첫 번째 파라미터로 전달해 결과 처리.
     *  </caption>
     *  //success: arguements 중 첫 번째 function 객체
     *  function(json){
     *    //json은 서버 서비스가 반환한 데이터
     *  }
     *  @example
     * <caption>
     *     <b>예시 1:</b>
     *     x_processplatform_assemble_surface 서비스로 현재 사용자의 결재 대기 목록 조회:<br/>
     *     해당 서비스의 조회 페이지에서 확인 가능, http://server/x_processplatform_assemble_surface/jest/index.html (v7.2 이전 버전은 포트 20020 추가 필요)<br/>
     *     다음 화면을 볼 수 있습니다:<img src="img/module/Actions/Actions.png"/>
     *     TaskAction의 V2ListPaging 서비스가 현재 사용자의 결재 대기를 나열하는 서비스임을 찾을 수 있습니다.<br/>
     *     이 서비스의 정보는 다음과 같습니다:<br/>
     *     1. actionName: TaskAction<br/>
     *     2. methodName: V2ListPaging<br/>
     *     3. url 매개변수 2개: page(페이지), size(페이지당 개수)<br/>
     *     4. 일련의 body 매개변수<br/>
     *     5. 이 서비스 메서드 유형은 POST<br/>
     *     이 정보로 다음과 같은 메서드를 구성할 수 있습니다:
     * </caption>
     * const processAction = this.Actions.load("x_processplatform_assemble_surface"); //action 조회
     * const method = processAction.TaskAction.V2ListPaging; //목록 메서드 조회
     * //실행 방법 1
     * method(
     *  1,  //uri 첫 번째 매개변수, uri 매개변수가 없으면 생략 가능
     *  20, //uri 두 번째 매개변수, uri 매개변수가 없으면 생략 가능, 다른 uri 매개변수가 있으면 쉼표(,)로 구분
     *  {   //body 매개변수, POST와 PUT 요청은 반드시 전달해야 하며 빈 객체 가능
     *      processList : [xxx] //구체적인 매개변수
     *  },
     *  function(json){ //정상 호출 콜백
     *       //json.data로 서비스 반환 데이터 획득
     *  },
     *  function(responseJSON){ //선택, 오류 정보, json 형식
     *      print( JSON.stringify(responseJSON) )
     *  }
     * );
     *
     * //실행 방법 2
     * const responsJSON = method( 1, 20, {processList : [xxx]} );
     * @example
     * <caption>오류 정보 responseJSON의 형식</caption>
     * {
     *       "type": "error", //유형은 오류
     *       "message": "식별자 343434 인 Task 객체가 존재하지 않습니다.", //안내 문구
     *       "date": "2020-12-29 17:02:13", //오류 발생 시간
     *       "prompt": "com.x.base.core.project.exception.ExceptionEntityNotExist" //서버 오류 클래스
     *}
     * @example
     * <caption>
     *     <b>예시 2:</b>
     *      프로세스 인스턴스의 workid를 알고 있을 때, 스크립트에서 데이터를 조회해 수정 후 저장.
     * </caption>
     * //서비스 목록에서 data 조회 서비스인 DataAction의 getWithWork 메서드 확인
     * //서비스 목록에서 data 갱신 서비스인 DataAction의 updateWithWork 메서드 확인
     *
     * const workid = "cce8bc22-225a-4f85-8132-7374d546886e";
     * const json = this.Actions.load("x_processplatform_assemble_surface").DataAction.getWithWork( //플랫폼에서 래핑한 메서드
     *      workid, //uri 매개변수
     * );
     * const data = json.data;
     *
     * data.subject = "새 제목"; //데이터 수정
     * const result = this.Actions.load("x_processplatform_assemble_surface").DataAction.updateWithWork(
     *      workid, //uri 매개변수
     *      data, //저장할 데이터
     * );
     */
    "load": function(root){
        if (this.loadedActions[root]) return this.loadedActions[root];
        const jaxrsString = globalThis.applications.describeApi(root);
        const json = JSON.parse(jaxrsString.toString());
        if (json && json.jaxrs){
            const actionObj = {};
            json.jaxrs.forEach((o)=>{
                if (o.methods && o.methods.length){
                    const actions = {};
                    o.methods.forEach((m)=>{
                        const o = {"uri": "/"+m.uri};
                        if (m.method) o.method = m.method;
                        if (m.enctype) o.enctype = m.enctype;
                        actions[m.name] = o;
                    });
                    actionObj[o.name] = new Action(root, actions);
                }
            });
            this.loadedActions[root] = actionObj;
            return actionObj;
        }
        return null;
    }
};

//log 출력
const _parsePrint = function(str, ...pars){
    if (!str && str!==0 && str!==false) return str;
    let text = (typeof str!=="string") ? str.toString() : str;
    try{
        text = text.replace(/%s/g, ()=>pars.shift()).toString();
        text += ' '+pars.map((p)=>p.toString()).join(' ')
    }catch(e){}
    return text;
}

/**
 * this.print는 서버 콘솔에 정보를 출력하는 메서드입니다.<br/>
 * @module print()
 * @o2cn 콘솔 출력
 * @o2category server.common
 * @o2ordernumber 152
 *
 * @param {(String)} text 출력할 텍스트 정보.</b>
 * @param {(String)} type 출력할 텍스트 정보의 유형, 출력 정보 앞에 추가됨, 기본값은 “PRINT”.</b>
 * @example
 * this.print("출력할 정보입니다");
 * //2021-12-20 13:26:24.739 [script] PRINT 출력할 정보입니다
 *
 * this.print("사용자 정의 유형 정보입니다", "MYTYPE");
 * //2021-12-20 13:26:24.765 [script] MYTYPE 사용자 정의 유형 정보입니다
 */
const print = function(text, type){
    // const d = new Intl.DateTimeFormat(undefined, {
    //     year: "numeric",
    //     month: "2-digit",
    //     day: "2-digit",
    //     hour: "2-digit",
    //     minute: "2-digit",
    //     second: "2-digit",
    //     fractionalSecondDigits: 3
    // }).format(new Date());
    var t = (type || "PRINT").toUpperCase();
    console[(t==='ERROR') ? 'error' : 'log'](`[script] ${t} ${text}`);
}

/**
 * this.log는 서버 콘솔 출력 메서드로, log, log.error, log.info, log.warn 메서드로 서버 콘솔에 정보를 출력할 수 있습니다.<br/>
 * @module log
 * @o2category server.common
 * @o2ordernumber 155
 * @o2cn 등급별 콘솔 출력
 * @example
 * log("출력할 정보입니다");
 * //2021-12-20 13:26:24.739 [script] PRINT 출력할 정보입니다
 *
 * log.error("오류 정보입니다");
 * //2021-12-20 13:26:24.765 [script] ERROR 오류 정보입니다
 *
 * log.info("일반 정보입니다");
 * //2021-12-20 13:26:24.765 [script] INFO 일반 정보입니다
 *
 * log.warn("경고 정보입니다");
 * //2021-12-20 13:26:24.765 [script] WARN 경고 정보입니다
 */
const log = function(...arg){
    print(_parsePrint(...arg));
}
const keys = ['error', 'info', 'warn'];
keys.forEach((k)=>{
    log[k] = function(...arg){
        print(_parsePrint(...arg), k);
    }
});

//조직 관련 서비스
const _getNameFlag = function(name){
    if (Array.isArray(name)){
        return name.map(n=>((typeof n)==="object" ? (n.distinguishedName || n.id || n.unique || n.name) : n));
    }else{
        return [((typeof name)==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name];
    }
}

/**
 * this.org로 조직의 사용자, 사용자 속성, 조직, 조직 속성, 신분, 그룹과 역할을 조회할 수 있습니다. 백엔드 호출은 모두 동기입니다.
 * @module server.org
 * @o2cn 조직 조회
 * @o2category server.common
 * @o2ordernumber 170
 * @since v9.0.4
 * @property    {GroupFactory}  group   백엔드 GroupFactory 인스턴스, group 그룹 관련 데이터 조회용
 * @property    {IdentityFactory}  identity   백엔드 IdentityFactory 인스턴스, identity 신분 관련 데이터 조회용
 * @property    {PersonFactory}  person   백엔드 PersonFactory 인스턴스, person 사용자 관련 데이터 조회용
 * @property    {PersonAttributeFactory}  personAttribute   백엔드 GroupFactory 인스턴스, personAttribute 사용자 속성 관련 데이터 조회용
 * @property    {RoleFactory}  role   백엔드 RoleFactory 인스턴스, role 역할 관련 데이터 조회용
 * @property    {UnitFactory}  unit   백엔드 UnitFactory 인스턴스, unit 관련 데이터 조회용
 * @property    {UnitAttributeFactory}  unitAttribute   백엔드 UnitAttributeFactory 인스턴스, unitAttribute 조직 속성 관련 데이터 조회용
 * @property    {UnitDutyFactory}  unitDuty   백엔드 UnitDutyFactory 인스턴스, unitDuty 조직 속성 관련 데이터 조회용
 * @o2syntax
 * //다음 참조로 현재 인스턴스의 org 객체를 가져올 수 있습니다:
 * this.org;
 * globalThis.org;
 * org
 *
 *@example
 * //백엔드 java 클래스로 현재 사용자가 속한 부서 이름 조회
 * const unit = org.unit();
 * const unitNames = unit.listWithPerson("홍길동@xxx@P");
 */
const org = {}
const _javaOrg = function(){
    return globalThis.java_resources.getOrganization();
}
Object.defineProperties(org, {
    oGroup:             { get: ()=>_javaOrg().group() },
    oIdentity:          { get: ()=>_javaOrg().identity() },
    oPerson:            { get: ()=>_javaOrg().person() },
    oPersonAttribute:   { get: ()=>_javaOrg().personAttribute() },
    oRole:              { get: ()=>_javaOrg().role() },
    oUnit:              { get: ()=>_javaOrg().unit() },
    oUnitAttribute:     { get: ()=>_javaOrg().unitAttribute() },
    oUnitDuty:          { get: ()=>_javaOrg().unitDuty() }
});
Object.assign(org,  {
    group: ()=> this.org.oGroup,
    identity: ()=> this.org.oIdentity,
    person: ()=> this.org.oPerson,
    personAttribute: ()=> this.org.oPersonAttribute,
    role: ()=> this.org.oRole,
    unit: ()=> this.org.oUnit,
    unitAttribute: ()=> this.org.oUnitAttribute,
    unitDuty: ()=> this.org.oUnitDuty,

    getObject(o, v){
        if (v && Array.isArray(v)){
            return v.map(name=>JSON.parse(o.getObject(name).toString()));
        }
        return null;
    },

    //그룹***************
    //그룹 조회--그룹 객체 배열 반환
    /**
     그룹 식별자로 해당 그룹 객체 또는 배열 조회: group 객체 또는 배열
     * @method getGroup
     * @o2membercategory group
     * @methodOf module:server.org
     * @static
     * @param {GroupFlag|GroupFlag[]} name - 그룹의 distinguishedName, name, id, unique 속성값, 그룹 객체 또는 이 속성값과 객체의 배열.
     * @return {GroupData|GroupData[]} 그룹 반환, 단일이면 Object, 복수면 Array.
     * @o2ActionOut x_organization_assemble_express.GroupAction.listObject|example=Group
     * @o2syntax
     * //그룹 반환, 단일이면 Object, 복수면 Array.
     * const groupList = this.org.getGroup( name );
     */
    getGroup: function(name){
        const v = this.oGroup.listObject(_getNameFlag(name));
        const v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
    },

    //하위 그룹 조회--그룹 객체 배열 반환
    //nested  불리언  true 중첩 하위; false 직접 하위; 기본값 false;
    /**
     그룹 식별자로 하위 그룹 객체 배열 조회: group 객체 배열.
     * @method listSubGroup
     * @o2membercategory group
     * @methodOf module:server.org
     * @static
     * @param {GroupFlag|GroupFlag[]} name - 그룹의 distinguishedName, name, id, unique 속성값, 그룹 객체 또는 이 속성값과 객체의 배열.
     * @param {Boolean} [nested]  true 중첩된 모든 하위 그룹, false 직속 하위 그룹, 기본값 false.
     * @return {GroupData[]} 그룹 배열 반환.
     * @o2ActionOut x_organization_assemble_express.GroupAction.listWithGroupSubDirectObject|example=Group
     * @o2syntax
     * //중첩 하위 그룹 배열 반환.
     * const groupList = this.org.listSubGroup( name, true );
     */
    listSubGroup: function(name, nested){
        return this.getObject(this.oGroup, this.oGroup[(nested) ? 'listWithGroupSubNested' : 'listWithGroupSubDirect'](_getNameFlag(name)));
    },


    //상위 그룹 조회--그룹 객체 배열 반환
    //nested  불리언  true 중첩 상위; false 직접 상위; 기본값 false;
    /**
     그룹 식별자로 상위 그룹 객체 배열 조회: group 객체 배열.
     * @method listSupGroup
     * @o2membercategory group
     * @methodOf module:server.org
     * @static
     * @param {GroupFlag|GroupFlag[]} name - 그룹의 distinguishedName, name, id, unique 속성값, 그룹 객체 또는 이 속성값과 객체의 배열.
     * @param {Boolean} [nested]  true 중첩된 모든 상위 그룹, false 직속 상위 그룹, 기본값 false.
     * @return {GroupData[]} 그룹 배열 반환.
     * @o2ActionOut x_organization_assemble_express.GroupAction.listWithGroupSupDirectObject|example=Group
     * @o2syntax
     * //중첩 상위 그룹 배열 반환.
     * const groupList = this.org.listSupGroup( name, true );
     */
    listSupGroup:function(name, nested){
        return this.getObject(this.oGroup, this.oGroup[(nested) ? 'listWithGroupSupNested' : 'listWithGroupSupDirect'](_getNameFlag(name)));
    },

    //사용자 소속 그룹(중첩)--그룹 객체 배열 반환
    /**
     * 사용자 식별자로 모든 그룹 객체 배열 조회. 그룹에 그룹(group) 구성원이 있고 그 구성원에 해당 사용자가 포함되면 그 그룹도 반환.
     * @method listGroupWithPerson
     * @o2membercategory group
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
     * @return {GroupData[]} 그룹 객체 배열 반환.
     * @o2ActionOut x_organization_assemble_express.GroupAction.listWithPersonObject|example=Group
     * @o2syntax
     * //그룹 배열 반환.
     * const groupList = this.org.listGroupWithPerson( name );
     */
    listGroupWithPerson:function(name){
        return this.getObject(this.oGroup, this.oGroup.listWithPerson(_getNameFlag(name)));
    },

    /**
     * 사용자 신분으로 모든 그룹 객체 배열 조회. 그룹에 그룹(group) 구성원이 있고 그 구성원에 해당 사용자와 신분이 포함되면 해당 그룹도 반환.
     * @method listGroupWithIdentity
     * @o2membercategory group
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 신분의 distinguishedName, id, unique 속성 값, 사용자 객체 또는 위 속성 값과 객체의 배열.
     * @return {GroupData[]} 그룹 객체 배열 반환.
     * @o2ActionOut x_organization_assemble_express.GroupAction.listWithPersonObject|example=Group
     * @o2syntax
     * //그룹 배열 반환.
     * const groupList = this.org.listGroupWithIdentity( name );
     */
    listGroupWithIdentity:function(name){
        return this.getObject(this.oGroup, this.oGroup.listWithIdentity(_getNameFlag(name)));
    },

    //그룹의 역할 보유 여부--true, false 반환
    /**
     * 그룹의 역할 보유 여부.
     * @method groupHasRole
     * @o2membercategory role
     * @methodOf module:server.org
     * @static
     * @param {GroupFlag} name - 그룹의 distinguishedName, name, id, unique 속성값, 그룹 객체.
     * @param {RoleFlag|RoleFlag[]} roleList - 역할의 distinguishedName, name, id, unique 속성값, 역할 객체 또는 이 속성값과 객체의 배열.
     * @return {Boolean} 그룹이 역할을 보유하면 true, 아니면 false 반환.
     * @o2syntax
     * //판단 결과 반환.
     * const groupList = this.org.groupHasRole( name, roleList );
     */
    groupHasRole: function(name, roleList){
        const nameFlag = (typeof name==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
        return this.oGroup.hasRole(nameFlag, _getNameFlag(roleList));
    },

    //역할***************
    //역할 조회--역할 객체 배열 반환
    /**
     * 역할 식별자로 해당 역할 객체 또는 배열 조회.
     * @method getRole
     * @o2membercategory role
     * @methodOf module:server.org
     * @static
     * @param {RoleFlag|RoleFlag[]} name - 역할의 distinguishedName, name, id, unique 속성값, 역할 객체 또는 이 속성값과 객체의 배열.
     * @return {RoleData|RoleData[]} 역할 반환, 단일이면 Object, 복수면 Array.
     * @o2ActionOut x_organization_assemble_express.RoleAction.listObject|example=Role
     * @o2syntax
     * //역할 반환, 단일이면 객체, 복수면 배열.
     * const roleList = this.org.getRole( name );
     */
    getRole: function(name){
        const v = this.oRole.listObject(_getNameFlag(name));
        const v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
    },

    //사용자의 모든 역할(중첩)--역할 객체 배열 반환
    /**
     * 사용자 식별자로 모든 역할 객체 배열 조회. 역할에 그룹(group) 구성원이 있고 그 그룹에 해당 사용자가 포함되면 그 역할도 반환.
     * @method listRoleWithPerson
     * @o2membercategory role
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
     * @return {RoleData[]} 역할 객체 배열 반환.
     * @o2ActionOut x_organization_assemble_express.RoleAction.listWithPersonObject|example=Role
     * @o2syntax
     * //역할 배열 반환.
     * const roleList = this.org.listRoleWithPerson( name );
     */
    listRoleWithPerson:function(name){
        return this.getObject(this.oRole, this.oRole.listWithPerson(_getNameFlag(name)));
    },

    //사용자***************
    //사용자의 역할 보유 여부--true, false 반환
    /**
     * 사용자의 역할 보유 여부.
     * @method personHasRole
     * @o2membercategory role
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag} name - 사용자의 distinguishedName, id, unique 속성값, 사용자 객체.
     * @param {RoleFlag|RoleFlag[]} roleList - 역할의 distinguishedName, name, id, unique 속성값, 역할 객체 또는 이 속성값과 객체의 배열.
     * @return {Boolean} 사용자가 역할을 보유하면 true, 아니면 false 반환.
     * @o2syntax
     * //판단 결과 반환.
     * const groupList = this.org.personHasRole( name, roleList );
     */
    personHasRole: function(name, roleList){
        const nameFlag = (typeof name==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name;
        return this.oPerson.hasRole(nameFlag, _getNameFlag(roleList));
    },

    //사용자 조회, 신분, 신분 소속 조직, 개인 소속 그룹, 개인 보유 역할 포함.
    /**
     사용자 식별자로 해당 사용자 객체 조회, 신분, 신분 소속 조직, 개인 소속 그룹, 개인 보유 역할 포함.
     * @method getPersonData
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {String} name - 사용자의 distinguishedName, id, unique 속성값, 사용자 이름.
     * @return {PersonData} 사용자 객체 반환.
     * @o2ActionOut x_organization_assemble_express.PersonAction.get|example=PersonData
     * @o2syntax
     * //사용자 객체 반환.
     * const person = this.org.getPersonData( name );
     */
    getPersonData: function(name){
        const v = this.oPerson.getExt((typeof name==="object") ? (name.distinguishedName || name.id || name.unique || name.name) : name);
        return (!v) ? null : JSON.parse(v.toString());
    },

    //사용자 조회--사용자 객체 배열 반환
    /**
     사용자 식별자로 해당 사용자 객체 또는 배열 조회: person 객체 또는 배열
     * @method getPerson
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
     * @param {(Boolean)} [findCN] 한글 이름(예: 홍길동)을 추가로 찾을지 여부, 기본값 false. true이면 unique와 distingiushedName 일치 외에 이름 첫 부분에서 일치하는 모든 사람을 찾음(정확히 일치).
     * @return {PersonData|PersonData[]} 사용자 반환, 단일이면 Object, 복수면 Array.
     * @o2ActionOut x_organization_assemble_express.PersonAction.listObject|example=Person
     * @o2syntax
     * //사용자 반환, 단일이면 객체, 복수면 배열.
     * const personList = this.org.getPerson( name );
     */
    getPerson: function(name, findCN){
        const v = this.oPerson.listObject(_getNameFlag(name), !!findCN);
        const v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
    },

    //하위 사용자 조회--사용자 객체 배열 반환
    //nested  불리언  true 중첩 하위; false 직접 하위; 기본값 false;
    /**
     사용자 식별자로 하위 사용자 객체 배열 조회: person 객체 배열. 상하 관계는 사용자의 보고 대상 값(superior)으로 결정.
     * @method listSubPerson
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
     * @param {Boolean} [nested]  true 중첩된 모든 하위 사용자, false 직속 하위 사용자, 기본값 false.
     * @return {PersonData[]} 사용자 배열 반환.
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithPersonSubDirectObject|example=Person
     * @o2syntax
     * //중첩 하위 사용자 배열 반환.
     * const personList = this.org.listSubPerson( name, true );
     */
    listSubPerson: function(name, nested){
        return this.getObject(this.oPerson, this.oPerson[(nested) ? 'listWithPersonSubNested' : 'listWithPersonSubDirect'](_getNameFlag(name)));
    },

    //상위 사용자 조회--사용자 객체 배열 반환
    //nested  불리언  true 중첩 상위; false 직접 상위; 기본값 false;
    /**
     *사용자 식별자로 상위 사용자 객체 배열 조회: person 객체 배열. 상하 관계는 사용자의 보고 대상 값(superior)으로 결정.
     * @method listSupPerson
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
     * @param {Boolean} [nested]  true 중첩된 모든 상위 사용자, false 직속 상위 사용자, 기본값 false.
     * @return {PersonData[]} 사용자 배열 반환.
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithPersonSupDirectObject|example=Person
     * @o2syntax
     * //중첩 상위 사용자 배열 반환.
     * const personList = this.org.listSupPerson( name, true );
     */
    listSupPerson: function(name, nested){
        return this.getObject(this.oPerson, this.oPerson[(nested) ? 'listWithPersonSupNested' : 'listWithPersonSupDirect'](_getNameFlag(name)));
    },

    //그룹의 모든 사용자 조회--사용자 객체 배열 반환
    /**
     * 그룹 식별자로 사용자 객체 구성원 조회: person 객체 배열.
     * @method listPersonWithGroup
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {GroupFlag|GroupFlag[]} name - 그룹의 distinguishedName, name, id, unique 속성값, 그룹 객체 또는 이 속성값과 객체의 배열.
     * @return {PersonData[]} 사용자 객체 배열 반환.
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithGroupObject|example=Person
     * @o2syntax
     * //사용자 배열 반환.
     * const personList = this.org.listPersonWithGroup( group );
     */
    listPersonWithGroup: function(name){
        return this.getObject(this.oPerson, this.oPerson.listWithGroup(_getNameFlag(name)));
    },

    //역할의 모든 사용자 조회--사용자 객체 배열 반환
    /**
     * 역할 식별자로 사용자 객체 배열 조회: person 객체 배열.
     * @method listPersonWithRole
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {RoleFlag|RoleFlag[]} name - 역할의 distinguishedName, name, id, unique 속성값, 역할 객체 또는 이 속성값과 객체의 배열.
     * @return {PersonData[]} 사용자 객체 배열 반환.
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithRoleObject|example=Person
     * @o2syntax
     * //사용자 배열 반환.
     * const personList = this.org.listPersonWithRole( role );
     */
    listPersonWithRole: function(name){
        return this.getObject(this.oPerson, this.oPerson.listWithRole(_getNameFlag(name)));
    },

    //신분의 모든 사용자 조회--사용자 객체 배열 반환
    /**
     * 신분 식별자로 사용자 객체 구성원 조회: person 객체 배열.
     * @method listPersonWithIdentity
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} name - 신분의 distinguishedName, id, unique 속성 값, 신분 객체, 또는 이 속성 값과 객체의 배열.
     * @return {PersonData[]} 사용자 객체 배열 반환.
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithIdentityObject|example=Person
     * @o2syntax
     * //사용자 배열 반환.
     * const personList = this.org.listPersonWithIdentity( identity );
     */
    listPersonWithIdentity: function(name){
        return this.getObject(this.oPerson, this.oPerson.listWithIdentity(_getNameFlag(name)));
    },

    //신분의 모든 사용자 조회--사용자 객체 배열 반환
    /**
     * 신분 식별자로 사용자 객체 조회: person 객체 배열.
     * @method getPersonWithIdentity
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} name - 신분의 distinguishedName, id, unique 속성 값, 신분 객체, 또는 이 속성 값과 객체의 배열.
     * @return {PersonData|PersonData[]} 사용자 객체 반환, 단일이면 Object, 여러 개면 Array.
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithIdentityObject|example=Person
     * @o2syntax
     * //사용자 반환, 단일이면 Object, 여러 개면 Array.
     * const personList = this.org.listPersonWithIdentity( identity );
     */
    getPersonWithIdentity: function(name){
        const v = this.oPerson.listWithIdentity(_getNameFlag(name));
        const arr = this.getObject(this.oPerson, v);
        return (arr && arr.length===1) ? arr[0] : arr;
    },

    //조직 구성원 사용자 조회--사용자 객체 배열 반환
    //nested  불리언  true 중첩된 모든 구성원; false 직접 구성원; 기본값 false;
    /**
     * 조직 식별자로 사용자 객체 구성원 조회: person 객체 배열.
     * @method listPersonWithUnit
     * @o2membercategory person
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체, 또는 이 속성 값과 객체의 배열.
     * @param {Boolean} [nested] 조직 및 하위 조직의 사용자를 중첩 조회할지 여부, true는 중첩, flase는 직속 조직 조회. 기본값 false
     * @return {PersonData[]} 사용자 객체 배열 반환.
     * @o2ActionOut x_organization_assemble_express.PersonAction.listWithUnitSubDirectObject|example=Person
     * @o2syntax
     * //조직의 직속 사용자 배열 반환.
     * const personList = this.org.listPersonWithUnit( unit );
     *
     * //조직 및 중첩 하위 조직의 모든 사용자 배열 반환.
     * const personList = this.org.listPersonWithUnit( unit, true );
     */
    listPersonWithUnit: function(name, nested){
        return this.getObject(this.oPerson, this.oPerson[(nested) ? 'listWithUnitSubNested' : 'listWithUnitSubDirect'](_getNameFlag(name)));
    },

    //사용자 속성************
    //사용자 속성 값 추가(속성에 values 값 추가, 해당 속성이 없으면 생성)
    /**
     * 사용자 속성 값 추가(속성에 values 값 추가, 해당 속성이 없으면 생성)
     * @method appendPersonAttribute
     * @o2membercategory personAttribute
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag} person - 사용자의 distinguishedName, id, unique 속성값, 사용자 객체.
     * @param {String} attr 속성 이름.
     * @param {String[]} values 속성값, 반드시 배열.
     * @o2syntax
     * //사용자 속성 값 추가
     * this.org.appendPersonAttribute( person, attribute, valueArray);
     */
    appendPersonAttribute: function(person, attr, values){
        const personFlag = (typeof person==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        return this.oPersonAttribute.appendWithPersonWithName(personFlag, attr, values);
    },

    //사용자 속성 값 설정(속성 값을 values로 변경, 해당 속성이 없으면 생성)
    /**
     * 사용자 속성 값 설정(속성 값을 values로 변경, 해당 속성이 없으면 생성)
     * @method setPersonAttribute
     * @o2membercategory personAttribute
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag} person - 사용자의 distinguishedName, id, unique 속성값, 사용자 객체.
     * @param {String} attr 속성 이름.
     * @param {String[]} values 속성값, 반드시 배열.
     * @o2syntax
     * //사용자 속성 값 추가
     * this.org.setPersonAttribute( person, attribute, valueArray);
     */
    setPersonAttribute: function(person, attr, values){
        const personFlag = (typeof person==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        return this.oPersonAttribute.setWithPersonWithName(personFlag, attr, values);
    },

    //사용자 속성 값 조회
    /**
     사용자와 속성 이름으로 속성값 배열 조회.
     * @method getPersonAttribute
     * @o2membercategory personAttribute
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag} person - 사용자의 distinguishedName, id, unique 속성값, 사용자 객체.
     * @param {String} attr 속성 이름.
     * @return {String[]} 속성 값 배열 반환,
     * 예: <pre><code class='language-js'>[ value1, value2 ]</code></pre>
     * @o2syntax
     * //res.blob()          //응답 내용 Response 스트림(java.io.ByteArrayInputStream)
     * const attributeList = this.org.getPersonAttribute( person, attr );
     */
    getPersonAttribute: function(person, attr){
        const personFlag = (typeof person==="object") ? (person.distinguishedName || person.id || person.unique || person.name) : person;
        const v = this.oPersonAttribute.listAttributeWithPersonWithName(personFlag, attr);
        if (v && v.length){
            return v.map(value=>value.toString())
        }
        return [];
    },

    //사용자의 모든 속성 이름 목록
    /**
     사용자의 모든 속성 이름 배열 나열.
     * @method listPersonAttributeName
     * @o2membercategory personAttribute
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
     * @return {String[]} 사용자 속성 이름 배열 반환,
     * 예: <pre><code class='language-js'>[ attributeName1, attributeName2 ]</code></pre>
     * @o2syntax
     * //사용자의 모든 속성 이름 배열 반환.
     * const attributeNameList = this.org.listPersonAttributeName( person );
     */
    listPersonAttributeName: function(name){
        const p = _getNameFlag(name);
        const nameList = [];
        for (let i=0; i<p.length; i++){
            const v = this.oPersonAttribute.listNameWithPerson(p[i]);
            if (v && v.length){
                for (let j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])===-1) nameList.push(v[j].toString());
                }
            }
        }
        return nameList;
    },

    //신분**********
    //신분 조회
    /**
     신분 식별자로 해당 신분 객체 또는 배열 조회
     * @method getIdentity
     * @o2membercategory identity
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} name - 신분의 distinguishedName, id, unique 속성 값, 신분 객체, 또는 이 속성 값과 객체의 배열.
     * @return {IdentityData|IdentityData[]} 신분 반환, 단일이면 Object, 여러 개면 Array.
     * @o2ActionOut x_organization_assemble_express.IdentityAction.listObject|example=Identity|ignoreNoDescr=true|ignoreProps=[woUnitDutyList,woUnit,woGroupList]
     * @o2syntax
     * //신분 반환, 단일이면 객체, 여러 개면 배열.
     * const identityList = this.org.getIdentity( name );
     */
    getIdentity: function(name){
        const v = this.oIdentity.listObject(_getNameFlag(name));
        const v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
    },

    /**
     신분 추가
     * @method addIdentity
     * @o2membercategory identity
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag} unit - 조직의 distinguishedName, id, unique 속성 값, 신분 객체 또는 이들 속성 값과 객체의 배열.
     * @param {PersonFlag} person - 사용자의 distinguishedName, id, unique 속성 값, 신분 객체 또는 이들 속성 값과 객체의 배열.
     * @param {String} name - 한글 이름.
     * @param {Object} data - 기타 값.
     * @return identityId 신분 id 반환.
     * @o2syntax
     * //신분 id 반환.
     * const identityId = this.org.addIdentity( unit, person, name );
     */
    addIdentity: function(unit, person, name, data){
        const unitFlag = _getNameFlag(unit);
        const personFlag = _getNameFlag(person);
        const identityList = Actions.load('x_organization_assemble_express').IdentityAction.listWithUnitPersonObject({
            unitList: unitFlag,
            personList: personFlag
        }).data;
        //존재하지 않으면 신분 생성
        if( !identityList || !identityList.length ) {
            return Actions.load("x_organization_assemble_control").IdentityAction.create({
                ...data,
                "name": name,
                "person": personFlag[0],
                "unit": unitFlag[0]
            }).data.id;
        }else{
            return Actions.load("x_organization_assemble_control").IdentityAction( identityList[0].unique ).data.id;
        }
    },

    //사용자의 신분 목록
    /**
     * 사용자 식별자로 해당 신분 객체 배열 조회.
     * @method listIdentityWithPerson
     * @o2membercategory identity
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
     * @return {IdentityData[]} 신분 객체 배열 반환.
     * @o2ActionOut x_organization_assemble_express.IdentityAction.listWithPersonObject|example=Identity
     * @o2syntax
     * //신분 객체 배열 반환.
     * const identityList = this.org.listIdentityWithPerson( person );
     */
    listIdentityWithPerson: function(name){
        return this.getObject(this.oIdentity, this.oIdentity.listWithPerson(_getNameFlag(name)));
    },

    //조직 구성원 신분 조회--신분 객체 배열 반환
    //nested  불리언  true 중첩된 모든 구성원; false 직접 구성원; 기본값 false;
    /**
     * 조직 식별자로 해당 신분 객체 배열 조회: identity 객체 배열.
     * @method listIdentityWithUnit
     * @o2membercategory identity
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체, 또는 이 속성 값과 객체의 배열.
     * @param {Boolean} [nested] true는 하위 포함 모든 신분 구성원, false는 직속 신분 구성원, 기본값 false.
     * @return {IdentityData[]} 신분 객체 배열 반환.
     * @o2ActionOut x_organization_assemble_express.IdentityAction.listWithUnitSubNestedObject|example=Identity
     * @o2syntax
     * //직속 조직 신분 객체 배열 반환.
     * const identityList = this.org.listIdentityWithUnit( unit );
     *
     * //중첩 조직 신분 객체 배열 반환.
     * const identityList = this.org.listIdentityWithUnit( unit, true );
     */
    listIdentityWithUnit: function(name, nested){
        return this.getObject(this.oIdentity, this.oIdentity[(nested) ? 'listWithUnitSubNested' : 'listWithUnitSubDirect'](_getNameFlag(name)));
    },

    //조직**********
    //조직 조회
    /**
     조직 식별자로 해당 조직 조회: unit 객체 또는 배열
     * @method getUnit
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체 또는 이들 속성 값과 객체의 배열.]
     * @param {(Boolean)} [findCN] 한글 이름(예: 종합부) 추가 검색 여부, 기본값 false. true이면 unique와 distingiushedName 일치 외에 이름의 첫 구간에서 일치하는 모든 부서를 검색(정확히 일치).
     * @return {UnitData|UnitData[]} 단일이면 Object, 여러 개면 Array.
     * @o2ActionOut x_organization_assemble_express.UnitAction.listObject|example=Unit
     * @o2syntax
     * //조직 반환, 단일이면 객체, 여러 개면 배열.
     * const unitList = this.org.getUnit( name );
     */
    getUnit: function(name, findCN){
        const v = this.oUnit.listObject(_getNameFlag(name), !!findCN);
        const v_json = (!v || !v.length) ? null: JSON.parse(v.toString());
        return (v_json && v_json.length===1) ? v_json[0] : v_json;
    },

    //조직의 하위 조회--조직 객체 배열 반환
    //nested  불리언  true 중첩 하위; false 직접 하위; 기본값 false;
    /**
     조직 식별자로 하위 조직 객체 배열 조회: unit 객체 배열.
     * @method listSubUnit
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체, 또는 이 속성 값과 객체의 배열.
     * @param {Boolean} [nested]  true 중첩된 모든 하위 조직, false 직속 하위 조직, 기본값 false.
     * @return {UnitData[]} 조직 배열 반환.
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitSubNestedObject|example=Unit
     * @o2syntax
     * //중첩 하위 조직 배열 반환.
     * const unitList = this.org.listSubUnit( name, true );
     */
    listSubUnit: function(name, nested){
        return this.getObject(this.oUnit, this.oUnit[(nested) ? 'listWithUnitSubNested' : 'listWithUnitSubDirect'](_getNameFlag(name)));
    },

    //조직의 상위 조회--조직 객체 배열 반환
    //nested  불리언  true 중첩 상위; false 직접 상위; 기본값 false;
    /**
     조직 식별자로 상위 조직 객체 배열 일괄 조회: unit 객체 배열.
     * @method listSupUnit
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체, 또는 이 속성 값과 객체의 배열.
     * @param {Boolean} [nested]  true 중첩된 모든 상위 조직, false 직속 상위 조직, 기본값 false.
     * @return {UnitData[]} 조직 배열 반환.
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitSupNestedObject|example=Unit
     * @o2syntax
     * //중첩 상위 조직 배열 반환.
     * const unitList = this.org.listSupUnit( name, true );
     */
    listSupUnit: function(name, nested){
        return this.getObject(this.oUnit, this.oUnit[(nested) ? 'listWithUnitSupNested' : 'listWithUnitSupDirect'](_getNameFlag(name)));
    },

    listSupUnitWithLevel: function(name, level){
        var supUnitList = this.listSupUnit( name, true);
        var unitList = this.getUnit( name );

        return [].concat(
            supUnitList,
            Array.isArray(unitList) ? unitList : [unitList]
        ).filter(function (u){
            return u.level === level;
        });
    },

    listSupUnitWithType: function(name, type){
        var supUnitList = this.listSupUnit( name, true);
        var unitList = this.getUnit( name );

        return [].concat(
            supUnitList,
            Array.isArray(unitList) ? unitList : [unitList]
        ).filter(function (u){
            return (u.typeList || []).contains( type );
        });
    },

    //개인 신분으로 조직 조회
    //flag 숫자    몇 번째 단계의 조직을 조회할지 나타냄
    //     문자열  지정 유형의 조직 조회를 나타냄
    //     비어 있음     직접 소속 조직 조회를 나타냄
    /**
     개인 신분으로 조직 조회: unit 객체 또는 배열.
     * @method getUnitByIdentity
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag} name - 신분의 distinguishedName, id, unique 속성값, 신분 객체.
     * @param {String|Number} [flag]  값이 숫자이면 몇 번째 계층의 조직을 조회.<br/> 값이 문자열이면 지정 유형의 조직을 조회.<br/> 값이 비어 있으면 직속 조직을 조회.
     * @return {UnitData} 해당 조직 객체 반환.
     * @o2ActionOut x_organization_assemble_express.UnitAction.getWithIdentityWithLevelObject|example=Unit
     * @o2syntax
     * //직속 조직 반환.
     * const unit = this.org.getUnitByIdentity( name );
     *
     * //첫 번째 단계 조직 반환.
     * const unit = this.org.getUnitByIdentity( name, 1 );
     *
     * * //유형이 company인 가장 가까운 상위 조직 반환.
     * const unit = this.org.getUnitByIdentity( name, "company" );
     */
    getUnitByIdentity: function(name, flag){
        let getUnitMethod = "current";
        let v;
        if (flag){
            if (typeof flag ==="string") getUnitMethod = "type";
            if (typeof flag ==="number") getUnitMethod = "level";
        }
        const n = _getNameFlag(name)[0];
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
        const o = this.getObject(this.oUnit, [v]);
        return (o && o.length===1) ? o[0] : o;
    },

    //신분 소속 조직의 모든 상위 조직 목록
    /**
     * 신분이 속한 조직 일괄 조회, 상위 조직 객체 재귀 조회.
     * @method listAllSupUnitWithIdentity
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} name - 신분의 distinguishedName, id, unique 속성 값, 신분 객체, 또는 이 속성 값과 객체의 배열.
     * @return {UnitData[]} 조직 배열 반환.
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithIdentitySupNestedObject|example=Unit
     * @o2syntax
     * //조직 배열 반환.
     * const unitList = this.org.listAllSupUnitWithIdentity( name );
     */
    listAllSupUnitWithIdentity: function(name){
        return this.getObject(this.oUnit, this.oUnit.listWithIdentitySupNested(_getNameFlag(name)));
    },

    //사용자가 속한 모든 조직 조회(직속 조직)
    /**
     * 개인 식별자로 조직 객체 구성원 일괄 조회: Unit 객체 배열.
     * @method listUnitWithPerson
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
     * @return {UnitData[]} 조직 배열 반환.
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithPersonObject|example=Unit
     * @o2syntax
     * //조직 배열 반환.
     * const unitList = this.org.listUnitWithPerson( name );
     */
    listUnitWithPerson: function(name){
        return this.getObject(this.oUnit, this.oUnit.listWithPerson(_getNameFlag(name)));
    },

    //사용자 소속 조직의 모든 상위 조직 목록
    /**
     * 개인 식별자로 소속 조직 및 모든 상위 조직 일괄 조회: Unit 객체 배열.
     * @method listAllSupUnitWithPerson
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {PersonFlag|PersonFlag[]} name - 사용자의 distinguishedName, id, unique 속성 값, 사용자 객체, 또는 이 속성 값과 객체의 배열.
     * @return {UnitData[]} 개인이 속한 조직과 모든 상위 조직 반환.
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithPersonSupNestedObject|example=Unit
     * @o2syntax
     * //조직 배열 반환.
     * const unitList = this.org.listAllSupUnitWithPerson( name );
     */
    listAllSupUnitWithPerson: function(name){
        return this.getObject(this.oUnit, this.oUnit.listWithPersonSupNested(_getNameFlag(name)));
    },

    //조직 속성으로 일치하는 모든 조직 조회
    /**
     * 조직 속성으로 일치하는 모든 조직 조회.
     * @method listUnitWithAttribute
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {String} name 조직 속성 이름.
     * @param {String} attribute 조직 속성 값.
     * @return {UnitData[]} 조직 배열 반환.
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitAttributeObject|example=Unit
     * @o2syntax
     * //조직 배열 반환.
     * const unitList = this.org.listUnitWithAttribute( attributeName, attributeName );
     */
    listUnitWithAttribute: function(name, attribute){
        return this.getObject(this.oUnit, this.oUnit.listWithUnitAttribute(name, attribute));
    },

    //조직 직무로 일치하는 모든 조직 조회
    /**
     * 조직 직무로 일치하는 모든 조직 조회.
     * @method listUnitWithDuty
     * @o2membercategory unit
     * @methodOf module:server.org
     * @static
     * @param {String} name 조직 직무 이름.
     * @param {IdentityFlag} id 신분의 distinguishedName, id, unique 속성 값, 신분 객체.
     * @return {UnitData[]} 조직 배열 반환.
     * @o2ActionOut x_organization_assemble_express.UnitAction.listWithUnitDutyObject|example=Unit
     * @o2syntax
     * //조직 배열 반환.
     * const unitList = this.org.listUnitWithDuty( dutyName, identity );
     */
    listUnitWithDuty: function(name, id){
        const idflag = (typeof id==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id;
        return this.getObject(this.oUnit, this.oUnit.listWithUnitDuty(name, idflag));
    },

    /**
     * 모든 최상위 조직 나열.
     * @method listTopUnit
     * @o2membercategory unit
     * @methodOf module:server
     * @static
     * @return {Promise|UnitData[]} async가 true이면 반환
     * @o2ActionOut x_organization_assemble_express.UnitAction.listObject|example=Unit
     * @o2syntax
     * //동기 실행, 최상위 조직 배열 반환.
     * var unitList = this.org.listTopUnit();
     */
    listTopUnit: function(){
        var action = Actions.load("x_organization_assemble_control").UnitAction;
        return action.listTop().data;
    },

    //조직 직무***********
    //지정 조직 직무의 신분 조회
    /**
     * 직무 이름과 조직 이름으로 신분 조회.
     * @method getDuty
     * @o2membercategory duty
     * @methodOf module:server.org
     * @static
     * @param {String} duty 조직 직무 이름.
     * @param {UnitFlag} id 조직의 distinguishedName, id, unique 속성 값, 조직 객체.
     * @return {IdentityData[]} 신분 배열 반환.
     * @o2ActionOut x_organization_assemble_express.UnitDutyAction.getWithUnitWithName|example=Identity
     * @o2syntax
     * //신분 배열 반환.
     * const identityList = this.org.getDuty( dutyName, unit );
     */
    getDuty: function(duty, id){
        const unit = (typeof id==="object") ? (id.distinguishedName || id.id || id.unique || id.name) : id;
        return this.getObject(this.oIdentity, this.oUnitDuty.listIdentityWithUnitWithName(unit, duty));
    },

    /**
     * 신분을 직무에 추가.
     * @method addIdentityToDuty
     * @o2membercategory duty
     * @methodOf module:server.org
     * @static
     * @param {String} dutyName 조직 직무 이름.
     * @param {UnitFlag} unit 조직의 distinguishedName, id, unique 속성값, 조직 객체.
     * @param {IdentityFlag} identity 신분의 distinguishedName, id, unique 속성 값, 조직 객체.
     * @return {dutyId} 신분 배열 반환.
     * @o2syntax
     * //직무 id 반환.
     * const dutyId = this.org.addIdentityToDuty( dutyName, unit, identity );
     */
    addIdentityToDuty: function (dutyName, unit, identity){
        const unitFlag = _getNameFlag(unit);
        const identityFlag = _getNameFlag(identity);
        const dutyActon = Actions.load("x_organization_assemble_control").UnitDutyAction;
        //현재 조직의 모든 직무 조회
        const dutyList = dutyActon.listWithUnit(unitFlag[0]);
        //같은 이름의 직무 존재 여부
        const curDutyList = dutyList.data.filter((duty)=>{
            return duty.name === dutyName;
        });
        //직무가 없으면 생성
        let dutyId;
        if( !curDutyList.length ){
            const json = dutyActon.create({
                "name" : dutyName,
                "unit" : unitFlag[0],
                "identityList" : identityFlag
            });
            dutyId = json.data.id;
        }else{
            const curDuty = curDutyList[0];
            const person = Actions.load("x_organization_assemble_express").PersonAction.listWithIdentityObject(identityFlag);
            const personId = person.data.length ? person.data[0].id : '';
            //현재 사용자가 직무의 신분 목록에 있는지 여부
            const exist = curDuty.woIdentityList.some((identity)=>{
                return identity.person === personId;
            });
            //존재하지 않으면 신분을 직무에 추가
            if( !exist ){
                curDuty.identityList.push(identityFlag[0]);
                dutyActon.edit(curDuty.id, curDuty);
            }
            dutyId = curDuty.id;
        }
        return dutyId;
    },

    //신분의 모든 직무 이름 조회
    /**
     * 신분의 모든 직무 이름 일괄 조회.
     * @method listDutyNameWithIdentity
     * @o2membercategory duty
     * @methodOf module:server.org
     * @static
     * @param {IdentityFlag|IdentityFlag[]} id - 신분의 distinguishedName, id, unique 속성 값, 신분 객체 또는 이들 속성 값과 객체의 배열.
     * @return {String[]} 직무 이름 배열 반환.
     * @o2syntax
     * //직무 이름 배열 반환.
     * const dutyNameList = this.org.listDutyNameWithIdentity( identity );
     */
    listDutyNameWithIdentity: function(id){
        const ids = _getNameFlag(id);
        const nameList = [];
        for (let i=0; i<ids.length; i++){
            const v = this.oUnitDuty.listNameWithIdentity(ids[i]);
            if (v && v.length){
                for (let j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])===-1) nameList.push(v[j].toString());
                }
            }
        }
        return nameList;
    },

    //조직의 모든 직무 이름 조회
    /**
     * 조직의 모든 직무 이름 일괄 조회.
     * @method listDutyNameWithUnit
     * @o2membercategory duty
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체, 또는 이 속성 값과 객체의 배열.
     * @return {String[]} 직무 이름 배열 반환.
     * @o2syntax
     * //직무 이름 배열 반환.
     * const dutyNameList = this.org.listDutyNameWithUnit( unit );
     */
    listDutyNameWithUnit: function(name){
        const ids = _getNameFlag(name);
        const nameList = [];
        for (let i=0; i<ids.length; i++){
            const v = this.oUnitDuty.listNameWithUnit(ids[i]);
            if (v && v.length){
                for (let j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])===-1) nameList.push(v[j].toString());
                }
            }
        }
        return nameList;
    },

    //조직의 모든 직무 조회
    /**
     * 조직의 모든 직무 일괄 조회.
     * @method listUnitAllDuty
     * @o2membercategory duty
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체.
     * @return {Object[]} 직무 배열 반환
     * @o2ActionOut x_organization_assemble_express.UnitDutyAction.listWithUnitObject|example=Duty
     * @o2syntax
     * //직무 배열 반환.
     * const dutyList = this.org.listUnitAllDuty( unit );
     */
    listUnitAllDuty: function(name){
        const u = _getNameFlag(name)[0];
        const ds = this.oUnitDuty.listNameWithUnit(u);
        const o = []
        for (let i=0; i<ds.length; i++){
            const v = this.oUnitDuty.listIdentityWithUnitWithName(u, ds[i]);
            o.push({"name": ds[i], "identityList": this.getObject(this.oIdentity, v)});
        }
        return o;
    },

    //조직 속성**************
    //조직 속성 값 추가(속성에 values 값 추가, 해당 속성이 없으면 생성)
    /**
     * 조직 속성 값 추가(속성에 values 값 추가, 해당 속성이 없으면 생성)
     * @method appendUnitAttribute
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag} unit - 조직의 distinguishedName, id, unique 속성값, 조직 객체.
     * @param {String} attr 속성 이름.
     * @param {String[]} values 속성값, 반드시 배열.
     * @o2syntax
     * this.org.appendUnitAttribute( unit, attribute, valueArray);
     */
    appendUnitAttribute: function(unit, attr, values){
        const unitFlag = (typeof unit==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        return this.oUnitAttribute.appendWithUnitWithName(unitFlag, attr, values);
    },

    //조직 속성 값 설정(속성 값을 values로 변경, 해당 속성이 없으면 생성)
    /**
     * 조직 속성 값 설정(속성 값을 values로 변경, 해당 속성이 없으면 생성)
     * @method setUnitAttribute
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag} unit - 조직의 distinguishedName, id, unique 속성값, 조직 객체.
     * @param {String} attr 속성 이름.
     * @param {String[]} values 속성값, 반드시 배열.
     * @o2syntax
     * this.org.setUnitAttribute( unit, attribute, valueArray);
     */
    setUnitAttribute: function(unit, attr, values){
        const unitFlag = (typeof unit==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        return this.oUnitAttribute.setWithUnitWithName(unitFlag, attr, values);
    },

    //조직 속성 값 조회
    /**
     조직 식별자와 속성 이름으로 해당 속성값 조회.
     * @method getUnitAttribute
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag} unit - 조직의 distinguishedName, id, unique 속성값, 조직 객체.
     * @param {String} attr 속성 이름.
     * @return {String[]} 속성 값 배열 반환,
     * 예: <pre><code class='language-js'>[ value1, value2 ]</code></pre>
     * @o2syntax
     * //해당 조직의 속성 값 배열 반환.
     * const attributeList = this.org.getUnitAttribute( unit, attr );
     */
    getUnitAttribute: function(unit, attr){
        const unitFlag = (typeof unit==="object") ? (unit.distinguishedName || unit.id || unit.unique || unit.name) : unit;
        const v = this.oUnitAttribute.listAttributeWithUnitWithName(unitFlag, attr);
        if (v && v.length){
            return v.map(value=>value.toString());
        }
        return [];
    },

    //조직의 모든 속성 이름 목록
    /**
     조직의 모든 속성 이름 배열 나열.
     * @method listUnitAttributeName
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체, 또는 이 속성 값과 객체의 배열.
     * @return {String[]} 조직 속성 이름 배열 반환,
     * 예: <pre><code class='language-js'>[ attributeName1, attributeName2 ]</code></pre>
     * @o2syntax
     * //조직의 모든 속성 이름 배열 반환.
     * const attributeNameList = this.org.listUnitAttributeName( unit );
     */
    listUnitAttributeName: function(name){
        const p = _getNameFlag(name);
        const nameList = [];
        for (let i=0; i<p.length; i++){
            const v = this.oUnitAttribute.listNameWithUnit(p[i]);
            if (v && v.length){
                for (let j=0; j<v.length; j++){
                    if (nameList.indexOf(v[j])===-1) nameList.push(v[j]);
                }
            }
        }
        return nameList;
    },

    //조직의 모든 속성 목록
    /**
     조직의 모든 속성 객체 배열을 나열합니다.
     * @method listUnitAllAttribute
     * @o2membercategory unitAttribute
     * @methodOf module:server.org
     * @static
     * @param {UnitFlag|UnitFlag[]} name - 조직의 distinguishedName, id, unique 속성 값, 조직 객체, 또는 이 속성 값과 객체의 배열.
     * @return {Object[]} 조직 속성 객체 배열 반환, 예:
     * <pre><code class='language-js'>[{
     *    "name": "부서 유형",
     *    "unit": "개발부@kfb@U",
     *    "attributeList": [
     *        "생산 부서",
     *        "2급 부서"
     *    ]
     * }]</code></pre>
     * @o2syntax
     * //조직의 모든 속성 객체 배열 반환.
     * const attributeObjectList = this.org.listUnitAllAttribute( unit );
     */
    listUnitAllAttribute: function(name){
        const u = _getNameFlag(name)[0];
        const ds = this.oUnitAttribute.listNameWithUnit(u);
        if (ds && ds.length){
            return ds.map( value=>{
                return {name: value, valueList: this.getUnitAttribute(u, value)};
            });
        }
        return [];
    }
})


//필요한 서비스 정의
const processActions = new Action("x_processplatform_assemble_surface", {
    "getDictionary": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{applicationFlag}"},
    "getDictRoot": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/data"},
    "getDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data"},
    "setDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data", "method": "PUT"},
    "addDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data", "method": "POST"},
    "deleteDictData": {"uri": "/jaxrs/applicationdict/{applicationDict}/application/{application}/{path}/data", "method": "DELETE"},
    "getScript": {"uri": "/jaxrs/script/{flag}/application/{applicationFlag}", "method": "POST"},
});
const cmsActions = new Action("x_cms_assemble_control", {
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
const portalActions = new Action("x_portal_assemble_surface", {
    "getDictionary": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}"},
    "getDictRoot": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}/data"},
    "getDictData": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}/{path}/data"},
    "setDictData": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}/{path}/data", "method": "PUT"},
    "addDictData": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}/{path}/data", "method": "POST"},
    "deleteDictData": {"uri": "/jaxrs/dict/{dictFlag}/portal/{portalFlag}/{path}/data", "method": "DELETE"},
    "getScript":  {"uri": "/jaxrs/script/portal/{portal}/name/{name}","method": "POST"}
});
const serviceActions = new Action("x_program_center", {
    "getDictionary": {"uri": "/jaxrs/dict/{id}"},
    "getDictRoot": {"uri": "/jaxrs/dict/{dictFlag}/data"},
    "getDictData": {"uri": "/jaxrs/dict/{dictFlag}/{path}/data"},
    "setDictData": {"uri": "/jaxrs/dict/{dictFlag}/{path}/data", "method": "PUT"},
    "addDictData": {"uri": "/jaxrs/dict/{dictFlag}/{path}/data", "method": "POST"},
    "deleteDictData": {"uri": "/jaxrs/dict/{dictFlag}/{path}/data", "method": "DELETE"},
    "getScript":  {"uri": "/jaxrs/script/name/{name}/imported","method": "GET"}
});


//include 스크립트 참조
//optionsOrName : {
//  type : "", 기본값 process, portal  process  cms 가능
//  application : "", 포털/프로세스/CMS의 이름/별칭/id, 기본값은 현재 앱
//  name : "" // 스크립트 이름/별칭/id
//}
//또는 name: "" // 스크립트 이름/별칭/id
const includedScripts = {};

/**
 * 프로세스, 포털, 콘텐츠 관리 또는 서비스 관리에서 스크립트 설정을 만들 수 있습니다.</br>
 * 기본적으로 시스템은 javascript 형식으로 편집합니다.
 * 이때 <code class='language-js'>this.include()</code>로 스크립트 설정을 참조할 수 있습니다.</br>
 * v9.2 이후 스크립트 설정 이름이 .json, .html 또는 .css로 끝나면 시스템이 해당 편집기를 로드합니다.
 * 이 경우 다음 방법으로 내용을 조회할 수 있습니다.
 * <pre><code class='language-js'>this.includeHmtl();
 * this.includeCss();
 * this.includeJson();
 * </code></pre>
 * @module server.common.include
 * @o2cn 스크립트 설정 관련 api
 * @o2category server.common
 * @o2ordernumber 175
 */

/**
 * this.include는 메서드로, 프로세스, 포털, 콘텐츠 관리 또는 서비스 관리에서 스크립트 설정을 만들었을 때 this.include()로 스크립트 설정을 참조할 수 있습니다.<br/>
 * v8.0 이후 버전에 서비스 관리의 스크립트 설정이 추가되었습니다.<br/>
 * @method include
 * @methodOf module:server.common.
 * @o2cn 스크립트 참조
 * @param {(String|Object)} optionsOrName 스크립트 식별 문자열 또는 객체.
 * <pre><code class='language-js'>
 *
 * //다른 앱의 스크립트 설정을 참조하려면 options를 Object로 설정;
 * this.include({
 *       //type: 앱 유형. portal  process  cms  service 가능. 프로세스 스크립트 기본값은 process, 서비스 관리에서는 기본값 service
 *       type : "portal",
 *       application : "홈", // 포털, 프로세스, CMS의 이름, 별칭, id. 서비스 관리 스크립트를 참조할 때는 이 매개변수를 무시함.
 *       name : "initScript" // 스크립트 설정의 이름, 별칭 또는 id
 * });
 *
 * //서비스 관리의 스크립트 참조
 * this.include({
 *   "type": "service",
 *   "name": "scriptName"
 * });
 *
 * //프로세스 관리의 스크립트 참조
 * this.include({
 *   "type": "process",
 *   "application": "appName",
 *   "name": "scriptName"
 * });
 *
 * //콘텐츠 관리의 스크립트 참조
 * this.include({
 *   "type": "cms",
 *   "application": "appName",
 *   "name": "scriptName"
 * });
 *
 * //포털 관리의 스크립트 참조
 * this.include({
 *   "type": "portal",
 *   "application": "appName",
 *   "name": "scriptName"
 * });
 * </code></pre>
 * @param {Function} [callback] 로드 후 실행할 콜백 메서드.
 *
 * @o2syntax
 * //양식, 프로세스, 뷰, 쿼리 뷰의 각 내장 스크립트에서 this.include()로 이 앱이나 다른 앱의 스크립트 설정을 참조할 수 있습니다. 예:
 * this.include( optionsOrName, callback )
 * @example
 * <caption>
 *    <b>예시 1:</b> 공용 스크립트에서 공문 관리의 모든 문서 종류를 조회하는 공용 메서드를 정의하고, 쿼리 구문에서 이 메서드로 JPQL을 조합.<br/>
 *     1. 콘텐츠 관리에 fileRes 앱이 있고, 이 앱에서 스크립트를 만들어 FileSql로 이름 짓고 메서드를 정의.
 *     <img src='img/module/include/server_define1.png' />
 * </caption>
 * //메서드 정의
 * this.define('getFileSQL',function(){
 *   const application = ['회사 발신 문서','부서 발신 문서','당위원회 발신 문서'];
 *   const apps = application.map((app)=>{
 *       return `o.applicationName = '${app}'`
 *   });
 *   return `(${apps.join(' OR ')})`;
 *});
 * @example
 * <caption>
 *      2. 쿼리 구문에서 이 메서드 사용.
 *     <img src='img/module/include/server_define2.png'/>
 * </caption>
 * this.include({
 *   type : 'cms',
 *   application : 'fileRes',
 *   name : 'FileSql'
 * });
 * const sql = this.getFileSQL();
 * return `SELECT o FROM com.x.processplatform.core.entity.content.Task o WHERE ${sql}`;
 */

/**
 * 스크립트 설정에 저장된 것이 html 파일(이름이 .html로 끝남)일 때, this.includeHtml로 html 내용을 가져올 수 있습니다.
 * @method includeHtml
 * @since v9.2
 * @methodOf module:server.common.include
 * @param {(String|Object)} optionsOrName 스크립트 식별 문자열 또는 객체.
 * <pre><code class='language-js'>
 * //현재 앱의 html(스크립트 설정 내)을 가져오려면 options를 String으로 설정합니다.
 * this.includeHtml("test.html") //스크립트 설정의 이름, 별칭 또는 id
 *
 * //다른 앱의 html을 가져오려면 options를 Object로 설정합니다;
 * this.includeHtml({
 *       //type: 앱 유형. portal  process  cms  service 가능.
 *       //이 옵션이 없거나 값이 빈 문자열이면 참조하는 스크립트와 참조되는 스크립트 설정의 유형이 같음을 의미함.
 *       //예를 들어 포털 A 앱 스크립트에서 포털 B 앱의 스크립트 설정을 참조하면 type은 생략 가능.
 *       type : "portal",
 *       application : "홈", // 포털, 프로세스, CMS의 이름, 별칭, id. 기본값은 현재 앱, 스크립트가 서비스 관리에 있으면 이 매개변수는 무시
 *       name : "test.html" // 스크립트 설정의 이름, 별칭 또는 id
 * })
 *
 * //서비스 관리의 html 가져오기
 * this.includeHtml({
 *   "type": "service",
 *   "name": "scriptName"
 * });
 *
 * //프로세스 관리의 html 가져오기
 * this.includeHtml({
 *   "type": "process",
 *   "application": "appName",
 *   "name": "scriptName"
 * });
 *
 * //콘텐츠 관리의 html 가져오기
 * this.includeHtml({
 *   "type": "cms",
 *   "application": "appName",
 *   "name": "scriptName"
 * });
 *
 * //포털 관리의 html 가져오기
 * this.includeHtml({
 *   "type": "portal",
 *   "application": "appName",
 *   "name": "scriptName"
 * });
 * </code></pre>
 * @param {Function} [callback] 로드 후 실행할 콜백 메서드
 * @param {Boolean} [async] 비동기 로드 여부, 기본값 true
 * @o2syntax
 * this.includeHtml( optionsOrName, callback, async )
 * @example
 * //동기로 html 가져오기(권장하지 않음)
 * //포털의 스크립트 라이브러리에 html을 설정, 이름은 test.html
 * var html = this.includeHtml({
 *   "type": "portal",
 *   "application": "appName", //포털 식별자
 *   "name": "test.html"
 * });
 */


/**
 * 스크립트 설정에 저장된 것이 css 파일(이름이 .css로 끝남)일 때, this.includeCss로 css 내용을 가져올 수 있습니다.
 * @method includeCss
 * @since v9.2
 * @methodOf  module:server.common.include
 * @param {(String|Object)} optionsOrName 스크립트 식별 문자열 또는 객체.
 * <pre><code class='language-js'>
 * //현재 앱의 css(스크립트 설정 내)를 가져오려면 options를 String으로 설정합니다.
 * this.includeCss("test.css") //스크립트 설정의 이름, 별칭 또는 id
 *
 * //다른 앱의 html을 가져오려면 options를 Object로 설정합니다;
 * this.includeCss({
 *       //type: 앱 유형. portal  process  cms  service 가능.
 *       //이 옵션이 없거나 값이 빈 문자열이면 참조하는 스크립트와 참조되는 스크립트 설정의 유형이 같음을 의미함.
 *       //예를 들어 포털 A 앱 스크립트에서 포털 B 앱의 스크립트 설정을 참조하면 type은 생략 가능.
 *       type : "portal",
 *       application : "홈", // 포털, 프로세스, CMS의 이름, 별칭, id. 기본값은 현재 앱, 스크립트가 서비스 관리에 있으면 이 매개변수는 무시
 *       name : "initScript" // 스크립트 설정의 이름, 별칭 또는 id
 * })
 *
 * //서비스 관리의 html 가져오기
 * this.includeCss({
 *   "type": "service",
 *   "name": "scriptName"
 * });
 *
 * //프로세스 관리의 html 가져오기
 * this.includeCss({
 *   "type": "process",
 *   "application": "appName",
 *   "name": "scriptName"
 * });
 *
 * //콘텐츠 관리의 html 가져오기
 * this.includeCss({
 *   "type": "cms",
 *   "application": "appName",
 *   "name": "scriptName"
 * });
 *
 * //포털 관리의 html 가져오기
 * this.includeCss({
 *   "type": "portal",
 *   "application": "appName",
 *   "name": "scriptName"
 * });
 * </code></pre>
 * @param {Function} [callback] 로드 후 실행할 콜백 메서드
 * @param {Boolean} [async] 비동기 로드 여부, 기본값 true
 * @o2syntax
 * this.includeCss( optionsOrName, callback, async )
 * @example
 * //동기로 css 텍스트 가져오기(권장하지 않음)
 * //포털의 스크립트 라이브러리에 css를 설정, 이름은 test.css
 * var cssText = this.includeCss({
 *   "type": "portal",
 *   "application": "appName", //포털 식별자
 *   "name": "test.css"
 * });
 */

/**
 * 스크립트 설정에 저장된 것이 json 파일(이름이 .json으로 끝남)일 때, this.includeJson으로 json 내용을 가져올 수 있습니다.
 * @method includeJson
 * @since v9.2
 * @methodOf  module:server.common.include
 * @param {(String|Object)} optionsOrName 스크립트 식별 문자열 또는 객체.
 * <pre><code class='language-js'>
 * //현재 앱의 json(스크립트 설정 내)을 가져오려면 options를 String으로 설정합니다.
 * this.includeJson("test.json") //스크립트 설정의 이름, 별칭 또는 id
 *
 * //다른 앱의 html을 가져오려면 options를 Object로 설정합니다;
 * this.includeJson({
 *       //type: 앱 유형. portal  process  cms  service 가능.
 *       //이 옵션이 없거나 값이 빈 문자열이면 참조하는 스크립트와 참조되는 스크립트 설정의 유형이 같음을 의미함.
 *       //예를 들어 포털 A 앱 스크립트에서 포털 B 앱의 스크립트 설정을 참조하면 type은 생략 가능.
 *       type : "portal",
 *       application : "홈", // 포털, 프로세스, CMS의 이름, 별칭, id. 기본값은 현재 앱, 스크립트가 서비스 관리에 있으면 이 매개변수는 무시
 *       name : "initScript" // 스크립트 설정의 이름, 별칭 또는 id
 * })
 *
 * //서비스 관리의 json 객체 가져오기
 * this.includeJson({
 *   "type": "service",
 *   "name": "scriptName"
 * });
 *
 * //프로세스 관리의 json 객체 가져오기
 * this.includeJson({
 *   "type": "process",
 *   "application": "appName",
 *   "name": "scriptName"
 * });
 *
 * //콘텐츠 관리의 json 객체 가져오기
 * this.includeJson({
 *   "type": "cms",
 *   "application": "appName",
 *   "name": "scriptName"
 * });
 *
 * //포털 관리의 json 객체 가져오기
 * this.includeJson({
 *   "type": "portal",
 *   "application": "appName",
 *   "name": "scriptName"
 * });
 * </code></pre>
 * @param {Function} [callback] 로드 후 실행할 콜백 메서드
 * @param {Boolean} [async] 비동기 로드 여부, 기본값 true
 * @o2syntax
 * this.includeJson( optionsOrName, callback, async )
 * @example
 * //동기로 json 데이터 가져오기(권장하지 않음)
 * //포털의 스크립트 라이브러리에 css를 설정, 이름은 test.json
 * var json = this.includeJson({
 *   "type": "portal",
 *   "application": "appName", //포털 식별자
 *   "name": "test.json"
 * });
 */

const include = function( optionsOrName , callback ){
    const options = (typeof optionsOrName) == "string" ? { name : optionsOrName } : optionsOrName;
    const name = options.name;

    const type = (!options.type) ? 'process' : options.type;
    const application = type === "service" ? "service" : options.application;

    if (!name || !type || !application){
        log.error("can not find script. missing script name or application");
        return false;
    }

    if (!includedScripts[application]) includedScripts[application] = [];

    if (includedScripts[application].indexOf( name )> -1){
        if (callback) callback.apply(globalThis);
        return;
    }

    const actionsMap = {
        portal: portalActions,
        process: processActions,
        cms: cmsActions,
        service: serviceActions,
    }

    const arg1 = (type==='portal') ? application : name;
    const arg2 = (type==='portal') ? name : application;
    const json = (type==='service') ?
        actionsMap[type].getScript(encodeURIComponent(name)) :
        actionsMap[type].getScript(encodeURIComponent(arg1), encodeURIComponent(arg2), {"importedList":includedScripts[application]});
    includedScripts[application] = includedScripts[application].concat(json.data.importedList);
    includedScripts[application].push(name);
    if (json.data && json.data.text){
        exec(json.data.text, globalThis);
        if (callback) callback.apply(globalThis);
    }
}

const _includeSource = function (optionsOrName, callback, fileType) {
    const options = (typeof optionsOrName) == "string" ? { name : optionsOrName } : optionsOrName;
    const name = options.name;

    const type = (!options.type) ? 'process' : options.type;
    const application = type === "service" ? "service" : options.application;

    if (!name || !type || !application){
        log.error("can not find script. missing script name or application");
        return false;
    }

    const actionsMap = {
        portal: portalActions,
        process: processActions,
        cms: cmsActions,
        service: serviceActions,
    }

    const arg1 = (type==='portal') ? application : name;
    const arg2 = (type==='portal') ? name : application;
    const json = (type==='service') ?
        actionsMap[type].getScript(encodeURIComponent(name)) :
        actionsMap[type].getScript(encodeURIComponent(arg1), encodeURIComponent(arg2), {"importedList":includedScripts[application]});

    let result;
    if (json.data && json.data.text){
        result = json.data.text;
        if( fileType === 'json' ){
            result = JSON.parse(result);
        }
        if (callback) callback.call(globalThis, result);
    }
    return result;
};
const includeHtml = function (optionsOrName, callback){
    return _includeSource.apply(this, [optionsOrName, callback, 'html'])
};
const includeJson = function (optionsOrName, callback){
    return _includeSource.apply(this, [optionsOrName, callback, 'json'])
};
const includeCss = function (optionsOrName, callback){
    return _includeSource.apply(this, [optionsOrName, callback, 'css']);
};

//Dict 데이터 사전
/**
 * this.Dict는 유틸리티 클래스로, 프로세스, 콘텐츠 관리, 포털, 서비스 관리에서 데이터 사전을 만들었다면 this.Dict 클래스로 데이터 사전을 추가·삭제·수정·조회할 수 있습니다.<br/>
 * v8.0부터 포털과 서비스 관리에서 데이터 사전 생성을 지원합니다.
 * @module server.Dict
 * @o2cn 데이터 사전
 * @o2category server.common
 * @o2ordernumber 180
 * @o2syntax
 * //this.Dict()로 이 앱이나 다른 앱의 데이터 사전 데이터를 추가·삭제·수정·조회할 수 있음. 예:
 * const dict = new this.Dict( options )
 * @example
 * const dict = new this.Dict({
 *     //type: 앱 유형. process  cms portal service 가능. 프로세스 스크립트 기본값은 process, 서비스 관리에서는 기본값 service.
 *    type : "cms",
 *    application : "bulletin", //프로세스, CMS, 포털 관리의 이름, 별칭, id. 서비스 관리의 데이터 사전을 참조할 때는 이 매개변수를 무시함.
 *    name : "bulletinDictionary", // 데이터 사전의 이름, 별칭, id
 * });
 *
 * //서비스 관리의 데이터 사전 참조
 * const dict = new this.Dict({
 *   "type": "service",
 *   "name": "dictName"
 * });
 *
 * //프로세스 관리의 데이터 사전 참조
 * const dict = new this.Dict({
 *   "type": "process",
 *   "application": "appName",
 *   "name": "dictName"
 * });
 *
 * //콘텐츠 관리의 데이터 사전 참조
 * const dict = new this.Dict({
 *   "type": "cms",
 *   "application": "appName",
 *   "name": "dictName"
 * });
 *
 * //포털 관리의 데이터 사전 참조
 * const dict = new this.Dict({
 *   "type": "portal",
 *   "application": "appName",
 *   "name": "dictName"
 * });
 */
const Dict = function(optionsOrName){
    const options = (typeof optionsOrName) == "string" ? { name : optionsOrName } : optionsOrName;
    const name = this.name = options.name;
    const type = (!options.type) ? 'process' : options.type;
    const applicationId = options.application || ((globalThis.java_workContext) ? globalThis.java_workContext.getWork().application : "");
    const enableAnonymous = options.enableAnonymous || false;

    const actionMap = {
        cms: cmsActions,
        service: serviceActions,
        portal: portalActions,
        process: processActions
    }
    const action = actionMap[type]

    const encodePath = function( path ){
        const arr = path.split(/\./g);
        const ar = arr.map(function(v){
            return encodeURIComponent(v);
        });
        return ( type === "portal" || type === "service" ) ? ar.join(".") : ar.join("/");
    };
    /**
     * 경로에 따라 데이터 사전의 데이터를 조회합니다.
     * @method get
     * @methodOf module:server.Dict
     * @static
     * @param {String} [path] 데이터 사전의 데이터 경로, 한글 사용 가능. 다단계 경로는 점(.)으로 구분. 값이 비어 있으면 데이터 사전의 모든 데이터를 조회.
     * @param {Function} [success] 데이터 조회 성공 시 콜백 함수. <b>프로세스 설계 백엔드 스크립트에는 이 매개변수가 없음.</b>
     * @param {Function} [failure] 데이터 조회 실패 시 콜백. <b>프로세스 설계 백엔드 스크립트에는 이 매개변수가 없음.</b>
     * @return {(Object|Array|String|Number|Boolean)}
     * 데이터 사전 데이터 반환, 유형은 데이터 사전 설정 시 지정한 것과 같음.
     * @o2syntax
     * const data = dict.get( path, success, failure )
     * @example
     * <caption>
     *     아래 그림과 같이 설정된 데이터 사전
     * <img src='img/module/Dict/dict.png' />
     * </caption>
     * const dict = new this.Dict({
     *     //type: 앱 유형. process  cms portal service 가능. 기본값은 process.
     *    type : "cms",
     *    application : "bulletin", //프로세스, CMS, 포털 관리의 이름, 별칭, id. 서비스 관리의 데이터 사전을 참조할 때는 이 매개변수를 무시함.
     *    name : "bulletinDictionary", // 데이터 사전의 이름, 별칭, id
     * });
     *
     * const data = dict.get();
     * //data의 값은
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "회사 공지",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 2.0,
     *            "text": "부서 공지",
     *            "value": "department"
     *        }
     *    ]
     * }
     *
     *  const category = dict.get("category");
     *  //category의 값은
     *  [
     *     {
     *        "enable": true,
     *        "sequence": 1.0,
     *        "text": "회사 공지",
     *        "value": "company"
     *    },
     *     {
     *       "enable": "false",
     *       "sequence": 2.0,
     *        "text": "부서 공지",
     *        "value": "department"
     *    }
     *  ]
     *
     *  const array0 = dict.get("category.0");
     *  //array0의 값은
     *  {
     *    "enable": true,
     *    "sequence": 1.0,
     *    "text": "회사 공지",
     *    "value": "company"
     * }
     *
     * const enable = dict.get("category.0.eanble");
     * //enable의 값은 true
     */
    this.get = function(path, success, failure){
        const getDataMethodName = (enableAnonymous && type === 'cms') ? 'getDictDataAnonymous' : 'getDictData';
        const getRootMethodName = (enableAnonymous && type === 'cms') ? 'getDictRootAnonymous' : 'getDictRoot';
        const methodName = (path) ? getDataMethodName : getRootMethodName;
        const successFun = json=>{if (success) success(json.data);}
        const failureFun = json=>{if (failure) failure(json.data);}

        const par = [encodeURIComponent(this.name)];
        if (type !== "service") par.push(encodeURIComponent(applicationId));
        if (path) par.push(encodePath(path));

        const json = action[methodName](...par, successFun, failureFun);
        return json.data;
    };
    /**
     * 경로에 따라 데이터 사전의 데이터 수정.
     * @method set
     * @methodOf module:Dict
     * @instance
     * @param {String} path 데이터 사전의 데이터 경로, 한글 사용 가능. 다단계 경로는 점(.)으로 구분. 데이터 경로가 없으면 오류 발생.
     * @param {(Object|Array|String|Number|Boolean)} value 수정 후 데이터
     * @param {Function} [success] 데이터 설정 성공 시 콜백 함수. <b>프로세스 설계 백엔드 스크립트에는 이 매개변수가 없음.</b>
     * @param {Function} [failure] 데이터 설정 오류 시 콜백 함수. <b>프로세스 설계 백엔드 스크립트에는 이 매개변수가 없음.</b>
     * @o2syntax
     * dict.set( path, data, success, failure )
     * @example
     * const dict = new this.Dict({
     *     //type: 앱 유형. process  cms portal service 가능. 기본값은 process.
     *    type : "cms",
     *    application : "bulletin", //프로세스, CMS, 포털 관리의 이름, 별칭, id. 서비스 관리의 데이터 사전을 참조할 때는 이 매개변수를 무시함.
     *    name : "bulletinDictionary", // 데이터 사전의 이름, 별칭, id
     * });
     *
     * dict.set( "category", { text : "시스템 공지", value : "system" }, function(data){
     *    //data 형식 예
     *    //{
     *    //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id는 데이터 사전 ID
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     * @example
     * <caption>
     *     Example add의 데이터 사전에 값 할당. 예:
     * </caption>
     * const dict = new this.Dict({
     *     //type: 앱 유형. process  cms portal service 가능. 기본값은 process.
     *    type : "cms",
     *    application : "bulletin", //프로세스, CMS, 포털 관리의 이름, 별칭, id. 서비스 관리의 데이터 사전을 참조할 때는 이 매개변수를 무시함.
     *    name : "bulletinDictionary", // 데이터 사전의 이름, 별칭, id
     * });
     *
     * dict.set( "archiveOptions", [ { text : "예" }, { text : "아니요" } ]);
     *      //데이터 사전 값이 다음과 같이 변경됨
     *  {
     *     "category": [
     *         {
     *             "enable": true,
     *             "sequence": 1.0,
     *             "text": "회사 공지",
     *             "value": "company"
     *         },
     *         {
     *             "enable": "false",
     *             "sequence": 2.0,
     *             "text": "부서 공지",
     *             "value": "department"
     *         },
     *         {
     *             "sequence" : 3.0,
     *             "text": "시스템 공지",
     *             "value": "system"
     *         }
     *
     *     ],
     *     "archiveOptions" : [ { text : "예" }, { text : "아니요" } ]
     * }
     *
     * dict.set( "category.2", { text : "현급 공지", value : "county" }, function(data){
     *     //data 형식 예
     *     //{
     *     //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id는 데이터 사전 ID
     *     //}
     *  }, function(xhr){
     *     //xhr은 xmlHttpRequest
     *  });
     *
     *   /데이터 사전 값이 다음과 같이 변경됨
     *  {
     *     "category": [
     *         {
     *             "enable": true,
     *             "sequence": 1.0,
     *             "text": "회사 공지",
     *             "value": "company"
     *         },
     *         {
     *             "enable": "false",
     *             "sequence": 2.0,
     *             "text": "부서 공지",
     *             "value": "department"
     *         },
     *         {
     *             "text": "현급 공지",
     *             "value": "county"
     *         }
     *     ],
     *     "archiveOptions" : [ { text : "예" }, { text : "아니요" } ]
     * }
     *
     * dict.set( "category.1.sequence", 3 );
     * dict.set( "category.2.sequence", 2 );
     *      //데이터 사전 값이 다음과 같이 변경됨
     *      {
     *     "category": [
     *         {
     *             "enable": true,
     *             "sequence": 1.0,
     *             "text": "회사 공지",
     *             "value": "company"
     *         },
     *         {
     *             "enable": "false",
     *             "sequence": 3.0,
     *             "text": "부서 공지",
     *             "value": "department"
     *         },
     *         {
     *             "sequence": 2.0,
     *             "text": "현급 공지",
     *             "value": "county"
     *         }
     *     ],
     *     "archiveOptions" : [ { text : "예" }, { text : "아니요" } ]
     * }
     * @example
     * <caption>
     *     다음은 잘못된 할당입니다:
     * </caption>
     * dict.set( "category_1", { text : "회사 공지" } ); //오류, category_1이 데이터 사전에 존재하지 않음
     */
    this.set = function(path, value, success, failure){
        const successFun = json=>{if (success) success(json.data);}
        const failureFun = json=>{if (failure) failure(json.data);}

        const par = [encodeURIComponent(this.name)];
        if (type !== "service") par.push(encodeURIComponent(applicationId));

        const json = action.setDictData(...par, encodePath(path), value, successFun, failureFun);
        return json.data;
    };

    /**
     * 경로에 따라 데이터 사전에 데이터를 추가합니다.
     * @method add
     * @methodOf module:Dict
     * @instance
     * @param {String} path 데이터 사전의 데이터 경로, 한글 사용 가능. 다단계 경로는 점(.)으로 구분. path에 이미 데이터가 있고 기존 데이터가 배열이면 배열에 항목 추가, 배열이 아니면 오류 발생.
     * @param {(Object|Array|String|Number|Boolean)} data 추가할 데이터
     * @param {Function} [success] 데이터 추가 성공 시 콜백 함수. <b>프로세스 설계 백엔드 스크립트에는 이 매개변수가 없음.</b>
     * @param {Function} [failure] 데이터 추가 오류 시 콜백 함수. <b>프로세스 설계 백엔드 스크립트에는 이 매개변수가 없음.</b>
     * @o2syntax
     * dict.add( path, data, success, failure )
     * @example
     * const dict = new this.Dict({
     *     //type: 앱 유형. process  cms portal service 가능. 기본값은 process.
     *    type : "cms",
     *    application : "bulletin", //프로세스, CMS, 포털 관리의 이름, 별칭, id. 서비스 관리의 데이터 사전을 참조할 때는 이 매개변수를 무시함.
     *    name : "bulletinDictionary", // 데이터 사전의 이름, 별칭, id
     * });
     *
     * dict.add( "category", { text : "시스템 공지", value : "system" }, function(data){
     *    //data 형식 예
     *    //{
     *    //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id는 데이터 사전 ID
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     * @example
     * <caption>
     *     get 메서드 예제의 데이터 사전에 값 할당, 예:
     * </caption>
     * const dict = new this.Dict({
     *     //type: 앱 유형. process  cms portal service 가능. 기본값은 process.
     *    type : "cms",
     *    application : "bulletin", //프로세스, CMS, 포털 관리의 이름, 별칭, id. 서비스 관리의 데이터 사전을 참조할 때는 이 매개변수를 무시함.
     *    name : "bulletinDictionary", // 데이터 사전의 이름, 별칭, id
     * });
     *
     * dict.add( "category", { text : "시스템 공지", value : "system" }, function(data){
     *    //data 형식 예
     *    //{
     *    //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id는 데이터 사전 ID
     *    //}
     * }, function(xhr, text, error){
     *    //xhr은 xmlHttpRequest, text는 오류 텍스트, error는 Error 객체
     * });
     *     //데이터 사전 값이 다음과 같이 변경됨
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "회사 공지",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 2.0,
     *            "text": "부서 공지",
     *            "value": "department"
     *        },
     *        {
     *            "text": "시스템 공지",
     *            "value": "system"
     *        }
     *    ]
     * }
     *
     *  dict.add( "category.2.sequence", 3 );
     *     //데이터 사전 값이 다음과 같이 변경됨
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "회사 공지",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 2.0,
     *            "text": "부서 공지",
     *            "value": "department"
     *        },
     *        {
     *            "sequence" : 3.0,
     *            "text": "시스템 공지",
     *            "value": "system"
     *        }
     *    ]
     * }

     * dict.add( "archiveOptions", {
     *    "yes" : "예",
     *    "no" : "아니요"
     * });
     *     //데이터 사전 값이 다음과 같이 변경됨
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "회사 공지",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 2.0,
     *            "text": "부서 공지",
     *            "value": "department"
     *        },
     *        {
     *            "sequence" : 3.0,
     *            "text": "시스템 공지",
     *            "value": "system"
     *        }
     *
     *    ],
     *    "archiveOptions" : {
     *        "yes" : "예",
     *        "no" : "아니요"
     *    }
     * }
     * @example
     * <caption>아래는 잘못된 할당 예:</caption>
     * dict.add( "category.3", { text : "시스템 공지", value : "system" }); //오류, 배열 인덱스에 직접 값을 할당할 수 없음
     *
     * dict.add( "category.1.value", { text : "시스템 공지" } ); //오류, 이미 존재하는 비배열 경로에는 값을 할당할 수 없음
     */
    this.add = function(path, value, success, failure){
        const successFun = json=>{if (success) success(json.data);}
        const failureFun = json=>{if (failure) failure(json.data);}

        const par = [encodeURIComponent(this.name)];
        if (type !== "service") par.push(encodeURIComponent(applicationId));

        const json = action.addDictData(...par, encodePath(path), value, successFun, failureFun);
        return json.data;
    };
    /**
     * 경로로 데이터 사전 데이터 삭제. <b>프로세스 설계 백엔드 스크립트에는 이 메서드가 없음.</b>
     * @method delete
     * @methodOf module:Dict
     * @instance
     * @param {String} path 데이터 사전의 데이터 경로, 한글 사용 가능. 다단계 경로는 점(.)으로 구분. 데이터 경로가 없으면 오류 발생.
     * @param {Function} [success] 데이터 삭제 성공 시 콜백 함수.
     * @param {Function} [failure] 데이터 삭제 오류 시 콜백 함수.
     * @o2syntax
     * dict.delete( path, success, failure )
     * @example
     * const dict = new this.Dict({
     *    //type: 앱 유형. process  cms portal service 가능. 기본값은 process.
     *    type : "cms",
     *    application : "bulletin", //프로세스, CMS의 이름·별칭·id, 기본값은 현재 앱
     *    name : "bulletinDictionary", //프로세스, CMS, 포털 관리의 이름, 별칭, id. 서비스 관리의 데이터 사전을 참조할 때는 이 매개변수를 무시함.
     * });
     *
     * dict.delete( "category", function(){
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     * @example
     * <caption>
     *     Example set의 데이터 사전에 값 할당. 예:
     * </caption>
     * const dict = new this.Dict("bulletinDictionary");
     *
     * dict.delete( "archiveOptions");
     * //데이터 사전 값이 다음과 같이 변경됨
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "회사 공지",
     *     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 3.0,
     *            "text": "부서 공지",
     *            "value": "department"
     *        },
     *        {
     *            "sequence": 2.0,
     *            "text": "현급 공지",
     *            "value": "county"
     *        }
     *    ]
     * }
     *
     * dict.delete( "category.2.sequence", function(data){
     *    //data 형식 예
     *    //{
     *    //    "id": "80ed5f60-500f-4358-8bbc-b7e81f77aa39" //id는 데이터 사전 ID
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     * //데이터 사전 값이 다음과 같이 변경됨
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "회사 공지",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 3.0,
     *            "text": "부서 공지",
     *            "value": "department"
     *        },
     *        {
     *            "text": "현급 공지",
     *            "value": "county"
     *        }
     *    ]
     * }
     *
     * dict.delete( "category.2");
     * //데이터 사전 값이 다음과 같이 변경됨
     * {
     *    "category": [
     *        {
     *            "enable": true,
     *            "sequence": 1.0,
     *            "text": "회사 공지",
     *            "value": "company"
     *        },
     *        {
     *            "enable": "false",
     *            "sequence": 3.0,
     *            "text": "부서 공지",
     *            "value": "department"
     *        }
     *    ]
     * }
     * @example
     * <caption>
     *     다음은 잘못된 삭제입니다:
     * </caption>
     * dict.delete( "category_1" ); //오류, category_1이 데이터 사전에 존재하지 않음
     */
    this["delete"] = function(path, success, failure){
        const successFun = json=>{if (success) success(json.data);}
        const failureFun = json=>{if (failure) failure(json.data);}

        const par = [encodeURIComponent(this.name)];
        if (type !== "service") par.push(encodeURIComponent(applicationId));

        const json = action.deleteDictData(...par, encodePath(path), successFun, failureFun);
        return json.data;
    };
    this.destory = this["delete"];
}

/**
 * this.Table은 도구 클래스로, 이 클래스로 데이터 센터의 데이터 테이블을 추가·삭제·수정·조회할 수 있습니다.
 * @module server.Table
 * @o2cn 데이터 테이블 실행
 * @o2category server.common
 * @o2ordernumber 185
 * @param {String} name 데이터 테이블의 id, 이름 또는 별칭.
 * @return {Object} table 객체
 * @o2syntax
 * //스크립트에서 this.Table()로 Table 객체를 반환받을 수 있음. 예:
 * const table = new this.Table( tableName )
 */
const Table = function(name){
    this.name = name;
    this.action = Actions.load("x_query_assemble_surface").TableAction;

    /**
     * 테이블의 행 객체 나열, 다음 페이지.
     * @method listRowNext
     * @methodOf module:server.Table
     * @instance
     * @param {String} id  현재 페이지 마지막 데이터의 Id, 첫 페이지면 "(0)" 또는 "0" 사용.
     * @param {String|Number} count 다음 페이지의 행 수
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @o2syntax
     * table.listRowNext( id, count, success, failure )
     * @example
     * const table = new this.Table("table1");
     *
     * table.listRowNext( "0", 20, function(data){
     *    //data 형식 예
     *    //{
     *    //    "type": "success",
     *    //    "data":[
     *    //       {
     *    //        "id": "5584e6d1-8088-4694-a948-8968ac8d4923", //데이터 id
     *    //        "createTime": "2021-11-01 16:23:41", //데이터 생성 시간
     *    //        "updateTime": "2021-11-01 16:23:41", //데이터 갱신 시간
     *    //         ... //정의된 필드(열)와 값
     *    //        }
     *   //     ],
     *   //       "message": "",
     *   //     "date": "2021-11-01 18:34:19",
     *   //     "spent": 13,
     *   //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */
    this.listRowNext = function(id, count, success, failure){
        return this.action.listRowNext(this.name, id, count, success, failure);
    };
    /**
     * 테이블의 행 객체 나열, 이전 페이지.
     * @method listRowPrev
     * @methodOf module:server.Table
     * @instance
     * @param {String} id  현재 페이지 첫 데이터의 Id, 마지막 페이지면 "(0)" 또는 "0" 사용.
     * @param {String|Number} count 이전 페이지의 행 수
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @o2syntax
     * table.listRowPrev( id, count, success, failure )
     * @example
     * const table = new this.Table("table1");
     *
     * table.listRowPrev( "0", 20, function(data){
     *    //data 형식 예
     *    //{
     *    //    "type": "success",
     *    //    "data":[
     *    //       {
     *    //        "id": "5584e6d1-8088-4694-a948-8968ac8d4923", //데이터 id
     *    //        "createTime": "2021-11-01 16:23:41", //데이터 생성 시간
     *    //        "updateTime": "2021-11-01 16:23:41", //데이터 갱신 시간
     *    //         ... //정의된 필드(열)와 값
     *    //        }
     *   //     ],
     *   //       "message": "",
     *   //     "date": "2021-11-01 18:34:19",
     *   //     "spent": 13,
     *   //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */
    this.listRowPrev = function(id, count, success, failure){
        return this.action.listRowPrev(this.name, id, count, success, failure);
    };
    /**
     * 조건에 따라 테이블 데이터 조회.
     * @method listRowSelect
     * @methodOf module:server.Table
     * @instance
     * @param {String} [where] 조회 조건, jpql 문법 형식, o.name='zhangsan', 빈 값 허용.
     * @param {String} [orderBy] 정렬 조건, 형식: o.updateTime desc, 빈 값 허용
     * @param {String|Number} [size] 반환 결과 개수, 빈 값 허용.
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @o2syntax
     * table.listRowSelect( where, orderBy, size, success, failure )
     * @example
     * const table = new this.Table("table1");
     *
     * //name 필드가 zhangsan인 데이터 조회, 결과는 updateTime 역순
     * table.listRowSelect( "o.name='zhangsan'", "o.updateTime desc", 20, function(data){
     *    //data 형식 예
     *    //{
     *    //    "type": "success",
     *    //    "data":[
     *    //       {
     *    //        "id": "5584e6d1-8088-4694-a948-8968ac8d4923", //데이터 id
     *    //        "createTime": "2021-11-01 16:23:41", //데이터 생성 시간
     *    //        "updateTime": "2021-11-01 16:23:41", //데이터 갱신 시간
     *    //         ... //정의된 필드(열)와 값
     *    //        }
     *   //     ],
     *   //       "message": "",
     *   //     "date": "2021-11-01 18:34:19",
     *   //     "spent": 13,
     *   //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */
    this.listRowSelect = function(where, orderBy, size, success, failure){
        return this.action.listRowSelect(this.name, {"where": where, "orderBy": orderBy, "size": size || ""}, success, failure);
    };
    this.listRowSelectWhere = function(where, success, failure){
        return this.action.listRowSelectWhere(this.name, where, success, failure);
    };
    /**
     * where로 개수 통계.
     * @method rowCountWhere
     * @methodOf module:server.Table
     * @instance
     * @param {String} where 쿼리 조건, jpql 문법 형식 o.name='zhangsan', 비워 둘 수 있음.
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @o2syntax
     * table.rowCountWhere( where, success, failure )
     * @example
     * const table = new this.Table("table1");
     *
     * //name 필드가 zhangsan인 데이터 조회, 결과는 updateTime 역순
     * table.rowCountWhere( "o.name='zhangsan'", function(data){
     *    //data 형식 예
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": 5 //조건에 맞는 데이터 총 건수
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */
    this.rowCountWhere = function(where, success, failure){
        return this.action.rowCountWhere(this.name, where, success, failure);
    };
    /**
     * 데이터 테이블에서 지정 id의 레코드 삭제.
     * @method deleteRow
     * @methodOf module:server.Table
     * @instance
     * @param {id} [id] 삭제할 레코드의 id.
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @o2syntax
     * table.deleteRow( id, success, failure )
     * @example
     * const table = new this.Table("table1");
     *
     * table.deleteRow( "e1f89185-d8b0-4b66-9e34-aed3323d0d79", function(data){
     *    //data 형식 예
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": true //true는 삭제 성공, false는 해당 데이터 없음
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */
    this.deleteRow = function(id, success, failure){
        return this.action.rowDelete(this.name, id, success, failure);
    };
    /**
     * 지정 테이블의 모든 행 데이터 삭제.
     * @method deleteAllRow
     * @methodOf module:server.Table
     * @instance
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @o2syntax
     * table.deleteAllRow( success, failure, async )
     * @example
     * const table = new this.Table("table1");
     *
     * table.deleteAllRow( function(data){
     *    //data 형식 예
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": 1 //삭제된 건수, 0은 데이터 없음
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */
    this.deleteAllRow = function(success, failure){
        return this.action.rowDeleteAll(this.name, success, failure);
    };
    /**
     * 데이터 테이블에서 지정 id의 레코드 조회.
     * @method getRow
     * @methodOf module:server.Table
     * @instance
     * @param {id} [id] 조회할 레코드의 id.
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @o2syntax
     * table.getRow( id, success, failure )
     * @example
     * const table = new this.Table("table1");
     *
     * table.getRow( "e1f89185-d8b0-4b66-9e34-aed3323d0d79", function(data){
     *    //data 형식 예
     *    //{
     *    //    "type": "success",
     *    //    "data":{
     *    //        "id": "5584e6d1-8088-4694-a948-8968ac8d4923", //데이터 id
     *    //        "createTime": "2021-11-01 16:23:41", //데이터 생성 시간
     *    //        "updateTime": "2021-11-01 16:23:41", //데이터 갱신 시간
     *    //         ... //정의된 필드(열)와 값
     *    //     },
     *   //     "message": "",
     *   //     "date": "2021-11-01 18:34:19",
     *   //     "spent": 13,
     *   //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */
    this.getRow = function(id, success, failure){
        return this.action.rowGet(this.name, id, success, failure);
    };
    /**
     * 데이터 테이블에 데이터 일괄 삽입.
     * @method insertRow
     * @methodOf module:server.Table
     * @instance
     * @param {Object[]} data 삽입할 데이터.
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @o2syntax
     * table.insertRow( data, success, failure )
     * @example
     * const table = new this.Table("table1");
     * const data = [
     *  {
     *    "subject": "제목 1",
     *    ... //기타 필드
     *  },
     *  ...
     * ];
     * table.insertRow( data, function(data){
     *    //data 형식 예
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": true //true는 삽입 성공
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */
    this.insertRow = function(data, success, failure){
        return this.action.rowInsert(this.name, data, success, failure);
    };
    /**
     * 데이터 테이블에 단건 데이터 삽입.
     * @method addRow
     * @methodOf module:Table
     * @instance
     * @param {Object} data 삽입할 데이터.
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @o2syntax
     * table.addRow( data, success, failure, async )
     * @example
     * const table = new this.Table("table1");
     * const data = {
     *    "subject": "제목 1",
     *    ... //기타 필드
     *  };
     * table.addRow( data, function(data){
     *    //data 형식 예
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "id": 2cf3a20d-b166-490b-8d29-05544db3d79b //true는 수정 성공
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */
    this.addRow = function(data, success, failure){
        return this.action.rowInsertOne(this.name, data, success, failure);
    };
    /**
     * 데이터 테이블의 단건 데이터 수정.
     * @method updateRow
     * @methodOf module:server.Table
     * @instance
     * @param {String} id 수정할 데이터 id.
     * @param {Object} data 수정할 데이터.
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @o2syntax
     * table.updateRow( id, data, success, failure )
     * @example
     * const table = new this.Table("table1");
     * const data = {
     *    "id" : "2cf3a20d-b166-490b-8d29-05544db3d79b",
     *    "subject": "제목 1",
     *    ... //기타 필드
     *  };
     * table.updateRow( "2cf3a20d-b166-490b-8d29-05544db3d79b", data, function(data){
     *    //data 형식 예
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": true //true는 수정 성공
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */
    this.updateRow = function(id, data, success, failure){
        return this.action.rowUpdate(this.name, id, data, success, failure);
    };

    /**
     * 데이터 테이블의 단건 데이터 부분 수정.
     * @method updateRow
     * @methodOf module:server.Table
     * @instance
     * @param {String} id 수정할 데이터 id.
     * @param {Object} data 수정할 일부 데이터, 나머지 데이터는 변경 없음.
     * @param {Function} [success] 호출 성공 시 콜백 함수.
     * @param {Function} [failure] 호출 오류 시 콜백 함수.
     * @o2syntax
     * table.updateRow( id, data, success, failure )
     * @example
     * const table = new this.Table("table1");
     * const data = {
     *    "id" : "2cf3a20d-b166-490b-8d29-05544db3d79b",
     *    "subject": "제목 1",
     *    ... //기타 필드
     *  };
     * table.updateRow( "2cf3a20d-b166-490b-8d29-05544db3d79b", data, function(data){
     *    //data 형식 예
     *    //{
     *    //   "type": "success",
     *    //  "data": {
     *    //      "value": true //true는 수정 성공
     *    //  },
     *    //  "message": "",
     *    //  "date": "2021-11-01 18:32:27"
     *    //}
     * }, function(xhr){
     *    //xhr은 xmlHttpRequest
     * });
     */
    this.partUpdateRow = function(id, data, success, failure){
        return this.action.rowPartUpdate(this.name, id, data, success, failure);
    };
}

/**
 * statement 객체로 쿼리 문을 실행하거나 쿼리 결과를 선택할 수 있습니다.<br/>
 * @module server.statement
 * @o2cn 쿼리 뷰 실행
 * @o2category server.common
 * @o2ordernumber 190
 * @o2syntax
 * //프로세스 양식, 콘텐츠 관리 양식, 포털 페이지 또는 뷰에서 this로 statement 객체를 가져올 수 있습니다. 예:
 * const statement = this.statement;
 */
const statement = {
    _parseParameter: function(obj, format){
        if(typeof obj !== 'object')return {};
        const parameter = {};
        //전달된 매개변수
        for (const [key, value] of Object.entries(obj)) {
            parameter[key] = (typeOf(value) === 'date') ? (['sql', 'sqlScript'].includes(format) ? value.format('db') : `{ts '${value.format('db')}'}`) : value;
        }
        return parameter;
    },
    _parseFilter: function(filter=[], parameter={}, format){
        return filter.map((d)=>{
            let pName = d.path.replace(/\./g, '_');

            pName = (parameter[pName]) ?
                (function unique(name, i){
                    return (parameter[name+'_'+i]) ? unique(name, ++i) : name+'_'+i;
                })(pName, 1) :
                pName;

            parameter[pName] = (d.comparison === 'like' || d.comparison === 'notLike') ?
                `%${d.value.replace(/^%|%$/g, '')}%` :
                (() => {
                    if (d.formatType === "numberValue") {
                        return parseFloat(d.value);
                    } else if ( ["dateTimeValue","dateValue","timeValue"].includes( d.formatType ) && !["sql", "sqlScript"].includes(format)) {
                        const prefix = {dateTimeValue: 'ts', datetimeValue: 'ts', dateValue: 'd', timeValue: 't'}[d.formatType];
                        return `{${prefix} '${d.value}'}`;
                    }
                    return d.value;
                })();

            d.value = pName;

            return d;
        });
    },
    _execute: function (statement, callback, format) {
        const parameter = this._parseParameter(statement.parameter, format);
        const filterList = this._parseFilter(statement.filter, parameter, format);
        const obj = {
            "filterList": filterList,
            "parameter" : parameter
        };

        return Actions.load("x_query_assemble_surface").StatementAction.executeV2(
            encodeURIComponent(statement.name), statement.mode || "data", statement.page || 1, statement.pageSize || 20, obj,
            callback
        );
    },
    _needCheckFormat: function(s){
        if (s.format) return false;

        if (typeof s.parameter === "object"){
            if (Object.values(s.parameter).findIndex(p=>typeOf(p)==='date')!==-1){
                return true;
            }
        }
        if( Array.isArray(s.filter)){
            if (s.filter.findIndex(f=>["dateTimeValue", "datetimeValue", "dateValue", "timeValue"].includes(f))!==-1){
                return true;
            }
        }
        return false;
    },
    /**
     * 지정한 쿼리 문을 실행합니다.
     * @method execute
     * @static
     * @param {Object} statement - 실행할 쿼리 문 정보. 데이터 형식은 다음과 같습니다:
     * <div>아래 filter 매개변수는 <a href='global.html#StatementFilter'>StatementFilter</a> 참고,
     * parameter 매개변수는 <a href='global.html#StatementParameter'>StatementParameter</a> 참고</div>
     * <pre><code class='language-js'>
     * {
     *  "name" : "tesStatement", //(String) 필수, 쿼리 설정의 이름·별칭 또는 ID
     *  "mode" : "all", //(String) 필수, “all”·“data”·“count” 중 하나. all은 쿼리 문과 총수 문을 함께 실행, data는 쿼리 문 실행, count는 총수 문 실행
     *  "page" : 1, //(number) 선택, 현재 페이지 번호, 기본값 1
     *  "pageSize" : 20, //(number) 선택, 페이지당 데이터 수, 기본값 20
     *  "filter": [ //(Array) 선택, 쿼리 필터 조건. json 배열 형식이며 각 배열 요소가 하나의 필터 조건, 각 요소의 데이터 형식은 다음과 같음:
     *       {
     *           "path":"o.title",  //쿼리 구문 형식이 jpql이면 o.title, 네이티브 sql이면 xtitle 사용
     *           "comparison":"like",
     *           "value":"관련",
     *           "formatType":"textValue"
     *       }
     *  ],
     *  parameter : {
     *       "person" : "", //매개변수 이름이 다음 값이면 백엔드에서 기본값 할당: person(현재 사용자), identityList(현재 사용자 신분 목록), unitList(현재 사용자의 직속 조직), unitAllList(현재 사용자의 모든 조직), groupList(현재 사용자의 그룹)
     *       "startTime" : (new Date("2020-01-01")), //날짜를 비교하는 경우 Date 타입 전달 필요
     *       "applicationName" : "%test%", //연산자가 like, noLike이면 모호 검색
     *       "processName" : "test프로세스", //그 외에는 확정된 값 입력
     *       "?1": "관련" //v8.0 이후 조회문은 물음표+숫자 형식 파라미터 지원
     *     }
     * }
     * </code></pre>
     * @param {Function} callback - 접근 성공 후 콜백 함수
     * @o2syntax
     * this.statement.execute(statement, callback, async);
     * @example
     * //“task” 쿼리의 데이터 조회
     * //쿼리 문은 select o from Task o where (o.person = :person) and (o.startTime > :startTime) and (o.applicationName like :applicationName) and (o.processName = :processName)
     * //총수 문은 select count(o.id) from Task o where (o.person = :person) and (o.startTime > :startTime) and (o.applicationName like :applicationName) and (o.processName = :processName)
     * //필터 조건은 제목 o.title이 “7월”을 포함(like).
     * this.statement.execute({
     *  "name": "task",
     *  "mode" : "all",
     *  "filter": [
     *      {
     *      "path":"o.title", //조회 구문 형식이 jpql이면 o.title, 네이티브 sql이면 xtitle 사용
     *      "comparison":"like",
     *      "value":"7월",
     *      "formatType":"textValue"
     *      }
     * ],
     * "parameter" : {
     *     "person" : "", //매개변수 이름이 다음 값이면 백엔드에서 기본값 할당: person(현재 사용자), identityList(현재 사용자 신분 목록), unitList(현재 사용자의 직속 조직), unitAllList(현재 사용자의 모든 조직), groupList(현재 사용자의 그룹)
     *     "startTime" : (new Date("2020-01-01")), //날짜를 비교하는 경우 Date 타입 전달 필요
     *     "applicationName" : "%test%", //연산자가 like, noLike이면 모호 검색
     *     "processName" : "test프로세스", //그 외에는 확정된 값 입력
     *     "?1": "관련" //v8.0 이후 조회문은 물음표+숫자 형식 파라미터 지원
     *   }
     * }, function(json){
     *  const count = json.count; //총수 구문 실행 후 반환된 숫자
     *  const list = json.data; //쿼리 구문 실행 후 반환된 배열
     *   //......
     * });
     */
    execute: function (statement, callback) {
        const jsonFormat = (!statement.format && this._needCheckFormat(statement)) ? Actions.load("x_query_assemble_surface").StatementAction.getFormat(statement.name).data : statement;
        return this._execute(statement, callback, (jsonFormat?.format || ""));
    },
    select: function () {}
};

/**
 * view 객체로 뷰 데이터를 조회하거나 뷰 데이터를 선택할 수 있습니다.<br/>
 * @module server.view
 * @o2cn 뷰 실행
 * @o2category server.common
 * @o2ordernumber 195
 * @o2syntax
 * //프로세스 양식, 콘텐츠 관리 양식 또는 포털 페이지에서 this로 view 객체를 가져올 수 있습니다. 예:
 * const view = this.view;
 */
const view = {
    /**
     * 지정한 뷰의 데이터를 조회합니다.
     * @method lookup
     * @static
     * @param {Object} view - 접근할 뷰 정보. 데이터 형식은 다음과 같습니다:<br/>
     * <caption>아래 filter 매개변수는 <a href='global.html#ViewFilter'>ViewFilter</a> 참고</caption>
     * <pre><code class='language-js'>
     * {
     *  "view" : "testView", //(String) 필수, 뷰의 이름·별칭 또는 ID
     *  "application" : "test데이터 센터 앱", //(String) 필수, 뷰가 속한 데이터 앱의 이름·별칭 또는 ID
     *  "filter": [ //(Array of Object) 선택, 뷰 필터 조건. json 배열 형식이며 각 배열 요소가 하나의 필터 조건을 나타냄.
     *       {
     *           "logic":"and",
     *           "path":"$work.title",
     *           "comparison":"like",
     *           "value":"7월",
     *           "formatType":"textValue"
     *       }
     *  ]
     * }
     * </code></pre>
     * @param {Function} callback - 접근 성공 후 콜백 함수
     * @param {Boolean} [async] - 동기 또는 비동기 호출. true: 비동기, false: 동기. 기본값 true.
     * @o2syntax
     * this.view.lookup(view, callback, async);
     * @example
     * //“재무 관리” 앱의 “경비 결재 데이터” 뷰의 데이터 조회
     * //필터 조건은 제목($work.title)이 “7월”을 포함(like).
     * this.view.lookup({
     *   "view": "경비 결재 데이터",
     *   "application": "재무 관리",
     *   "filter": [
     *       {
     *           "logic":"and",
     *           "path":"$work.title",
     *           "comparison":"like",
     *           "value":"7월",
     *           "formatType":"textValue"
     *       }
     *   ]
     *}, function(data){
     *   const grid = data.grid; //필터링된 데이터
     *   const groupGrid = data.groupGrid; //분류가 있으면 분류된 데이터
     *   //......
     *});
     * @example
     * //“재무 관리” 앱의 “경비 결재 데이터” 뷰의 데이터 조회
     * //필터 조건은 제목($work.title)이 “7월”을 포함(like)하고 총금액이 500보다 크고 5000보다 작음
     * this.view.lookup({
     *   "view": "경비 결재 데이터",
     *   "application": "재무 관리",
     *   "filter": [
     *       {
     *           "logic":"and",
     *           "path":"$work.title",
     *           "comparison":"like",
     *           "value":"7월",
     *           "formatType":"textValue"
     *       },
     *       {
     *           "logic":"and",
     *           "path":"amount",
     *           "comparison":"range",
     *           "value":500,
     *           "otherValue":5000,
     *           "formatType":"numberValue"
     *       },
     *   ]
     *}, function(data){
     *   const grid = data.grid; //필터링된 데이터
     *   const groupGrid = data.groupGrid; //분류가 있으면 분류된 데이터
     *   //......
     *});
     */
    "lookup": function(view, callback, person){
        const filterList = {"filterList": (view.filter || null)};

        const json = Actions.load("x_query_assemble_surface").ViewAction.executeWithQuery(encodeURIComponent(view.view), encodeURIComponent(view.application), filterList, null, null, person);

        const data = {
            "grid": json.data.grid || json.data.groupGrid,
            "groupGrid": json.data.groupGrid
        };
        if (callback) callback(data);

        return data;
    },
    "select": function(view, callback, options){}
};

/**
 * service 객체로 restful 요청 또는 soap 프로토콜 webservice 호출 가능.
 * @module service
 * @o2cn 공용 service 호출
 * @o2category server.common
 * @o2syntax
 * const service = this.service;
 * @example
 * //get 메서드로 restful 요청을 보내 json 데이터 조회
 * const res = this.service.restful("get", "config/myjson.json");
 * if (res.responseCode>=200 && responseCode<300){
 *     const jsonData = res.json;
 * }
 */
const service = {
    /**
     * restful 요청 전송.
     * @method restful
     * @o2category server.common
     * @param {String} [method] - restful 요청 메서드: get, post, put, delete ...
     * @param {String} [url] - restful 요청 주소
     * @param {Object} [headers] - 선택, json 객체, 요청 header, 기본 content-type: “application/json charset=utf-8”
     * @param {String|Object} [body] - 선택, post, put 요청의 메시지 본문, 텍스트 또는 json 객체 전달
     * @param {Number} [connectTimeout] - 선택, 연결 시간 제한(밀리초), 기본값은 2000.
     * @param {Number} [readTimeout] - 선택, 전송 시간 제한(밀리초), 기본값은 300000.
     * @return {Object} json 형식의 요청 결과 객체 반환, 형식은 다음과 같음:
     * <pre><code class='language-js'>
     * {
     *  "responseCode" : 200,   //요청 반환 code
     *  "headers" : {},         //응답 헤더
     *  "body": "",             //응답 body 텍스트 내용
     *  "json": {}              //응답 body의 json 형식 내용
     * }
     * </code></pre>
     * @o2syntax
     * var res = this.service.restful(method, url, headers, body, connectTimeout, readTimeout);
     * @example
     * //get 메서드로 restful 요청을 보내 json 데이터 조회
     * var res = this.service.restful("get", "config/myjson.json");
     * if (res.responseCode>=200 && res.responseCode<300){
     *     var jsonData = res.json;
     * }
     */
    restful: function(method, url, headers, body, connectTimeout=2000, readTimeout=300000){
        const service = globalThis.java_resources.getWebservicesClient();
        const bodyData = ((typeof body)==="object") ? JSON.stringify(body) : (body||"");
        const res = service.restful(method, url, (headers||null), bodyData, (connectTimeout||2000), (readTimeout||300000));

        const o = {
            "responseCode" : res.getResponseCode(),
            "headers" : res.getHeaders(),
            "body": res.getBody()
        }
        try {
            o.json = JSON.parse(o.body);
        }catch(e){}
        return o;
    },

    /**
     * get 방식으로 restful 요청 실행.
     * @method get
     * @methodOf restful
     * @static
     * @param {String} [url] - restful 요청 주소
     * @param {Object} [headers] - 선택, json 객체, 요청 header, 기본 content-type: “application/json charset=utf-8”
     * @param {Number} [connectTimeout] - 선택, 연결 시간 제한(밀리초), 기본값은 2000.
     * @param {Number} [readTimeout] - 선택, 전송 시간 제한(밀리초), 기본값은 300000.
     * @return {Object} json 형식의 요청 결과 객체 반환, 형식은 다음과 같음:
     * <pre><code class='language-js'>
     * {
     *  "responseCode" : 200,   //요청 반환 code
     *  "headers" : {},         //응답 헤더
     *  "body": "",             //응답 body 텍스트 내용
     *  "json": {}              //응답 body의 json 형식 내용
     * }
     * </code></pre>
     * @o2syntax
     * var res = this.service.get(url, headers, connectTimeout, readTimeout);
     */
    "get": function(url, headers, connectTimeout, readTimeout){
        return this.restful("get", url, headers, "", connectTimeout, readTimeout);
    },

    /**
     * post 방식으로 restful 요청 실행.
     * @method post
     * @static
     * @param {String} [url] - restful 요청 주소
     * @param {Object} [headers] - 선택, json 객체, 요청 header, 기본 content-type: “application/json charset=utf-8”
     * @param {String|Object} [body] - 선택, post, put 요청의 메시지 본문, 텍스트 또는 json 객체 전달
     * @param {Number} [connectTimeout] - 선택, 연결 시간 제한(밀리초), 기본값은 2000.
     * @param {Number} [readTimeout] - 선택, 전송 시간 제한(밀리초), 기본값은 300000.
     * @return {Object} json 형식의 요청 결과 객체 반환, 형식은 다음과 같음:
     * <pre><code class='language-js'>
     * {
     *  "responseCode" : 200,   //요청 반환 code
     *  "headers" : {},         //응답 헤더
     *  "body": "",             //응답 body 텍스트 내용
     *  "json": {}              //응답 body의 json 형식 내용
     * }
     * </code></pre>
     * @o2syntax
     * var res = this.service.post(url, headers, body, connectTimeout, readTimeout);
     */
    "post": function(url, headers, body, connectTimeout, readTimeout){
        return this.restful("post", url, headers, body, connectTimeout, readTimeout);
    },

    /**
     * soap 프로토콜의 webservice 요청 실행.
     * @method soap
     * @o2category server.common
     * @param {String} [wsdl] - wsdl 파일 주소
     * @param {String} [method] - 호출할 메서드 이름
     * @param {Array} [pars] - 메서드에 필요한 파라미터
     * @return {Object} 서비스가 반환하는 유형에 따라 다름:
     * @o2syntax
     * var res = this.service.soap(wsdl, method, pars);
     * @example
     * //webservice로 사용자 조회 예시
     * var res = this.service.soap("wsdl/mywsdl.wsdl", "getPerson", ["홍길동", "김철수"]);
     */
    soap: function(wsdl, method, pars){
        const service = globalThis.java_resources.getWebservicesClient();
        return service.soap(wsdl, method, pars);
    },
    soapXml: function(wsdl, xml){
        const service = globalThis.java_resources.getWebservicesClient();
        return service.jaxwsXml(wsdl, xml);
    }
}

//=========================================================================================/
//====이상은 공통 객체와 메서드로, 서버 스크립트에서 공통으로 사용================================================/
//=========================================================================================/

/*****************************************************************/
/* response - 서비스 호출 활동, 응답 스크립트, 응답 내용 조회 ******************/
/*            서비스 관리 invoke, 응답 내용 설정용  *******************/
/*    - 인터페이스 응답 객체, 서버 주입 객체 java_customResponse  **********/
/*    - java_jaxrsResponse, type()  ******************************/
/*      java_jaxrsResponse, get() 문자열  ************************/
/*      java_jaxwsResponse  처리 없이 바로 반환  *********************/
/*****************************************************************/
const responseDescriptor = {
    get: function(){
        if (globalThis.java_customResponse){   //java_customResponse 객체 주입, 스크립트가 서비스 관리 invoke 에서 실행됨을 의미
            /**
             * 서비스 관리의 인터페이스 스크립트에 사용되며, 서비스의 응답 객체를 나타냄.<br>
             * @o2range 서비스 관리-인터페이스
             * @o2cn 서비스의 응답 객체
             * @module server.service.response
             * @o2category server.service
             * @o2ordernumber 245
             * @o2syntax
             * const res = this.response;
             */
            return {
                /**
                 * @summary 서비스가 303 리다이렉트를 반환합니다.
                 * @method seeOther
                 * @methodOf service.service.module:response
                 * @static
                 * @param {String} [url] 리다이렉트할 url.
                 * @o2syntax
                 * this.response.seeOther(url);
                 */
                seeOther: function(url){
                    globalThis.java_customResponse.seeOther(url);
                },
                /**
                 * @summary 서비스가 301 리다이렉트를 반환합니다.
                 * @method redirect
                 * @methodOf service.service.module:response
                 * @static
                 * @param {String} [url] 리다이렉트할 url.
                 * @o2syntax
                 * this.response.redirect(url);
                 */
                redirect: function(url){
                    globalThis.java_customResponse.temporaryRedirect(url);
                },
                /**
                 * @summary 서비스 정상 데이터 반환(200).
                 * @method setBody
                 * @methodOf service.service.module:response
                 * @static
                 * @param {String|Object} [body] 응답 내용, 텍스트 또는 json 객체.
                 * @param {String} [contentType] 응답 헤더의 Content-Type.
                 * @o2syntax
                 * this.response.setBody(body, contentType);
                 * @example
                 * //json 형식의 응답 내용 설정
                 * this.response.setBody({
                 *     "key1": "value1",
                 *     "key2": "value2"
                 * }, "application/json");
                 */
                setBody: function(body, contentType=''){
                    const o = (typeof body==='object') ? JSON.stringify(body) : body;
                    globalThis.java_customResponse.setBody(o, contentType);
                },
                customResponse: globalThis.java_customResponse
            }
        }
        if (globalThis.java_jaxrsResponse) {   //java_jaxrsResponse 객체 주입, 스크립트가 서비스 호출 활동, 응답 스크립트 에서 실행되며 호출 유형이 jaxrs 임을 의미
            /**
             * 프로세스 설정의 서비스 호출 활동 「응답 스크립트」에서 사용하며, 서비스 호출 후 받은 응답을 나타냅니다.(주의: 호출 방식이 비동기이면 「응답 스크립트」는 실행되지 않습니다)<br>
             * @o2range 프로세스 설정-서비스 호출 활동의 「응답 스크립트」
             * @module server.response
             * @o2cn 서비스 호출 응답
             * @o2category server.process
             * @o2ordernumber 230
             * @o2syntax
             * const res = this.response;
             */
            return {
                /**
                 * @summary jaxrs 방식 서비스 호출 활동의 요청 반환 상태. 가능한 값은 네 가지:
                 * <pre><code class="language-js">success : 요청 성공
                 * warn : 플랫폼 내부 서비스를 호출할 때 요청은 성공했지만 경고 발생
                 * error : 요청 호출 오류
                 * connectFatal : 네트워크 연결 오류</code></pre>
                 * @member {String} status
                 * @memberOf server.module:response
                 * @o2syntax
                 * const status = this.response.status;
                 */
                get status(){
                    return globalThis.java_jaxrsResponse.type() || '';
                },
                /**
                 * @summary 요청 반환 값. jaxrs 서비스는 응답 텍스트를 받고, jaxws 서비스는 응답 유형이 서비스 반환에 따름.
                 * @member {String|Any} value
                 * @memberOf server.module:response
                 * @o2syntax
                 * const value = this.response.value;
                 * @example
                 * //json 객체 반환 정보 처리
                 * const value = this.response.value;
                 * const resData = JSON.parse(value);
                 */
                get value(){
                    return globalThis.java_jaxrsResponse.get();
                },
                /**
                 * @summary 요청 반환 값 조회. jaxrs 서비스는 응답 데이터를 가능한 한 json 객체로 변환하고, json 객체로 해석할 수 없으면 텍스트를 반환. jaxws 서비스는 응답 유형이 서비스 반환에 따름.
                 * @method get
                 * @methodOf server.module:response
                 * @static
                 * @return {Object|String|Any} 응답 내용.
                 * @o2syntax
                 * const res = this.response.get();
                 */
                get(){
                    const value = globalThis.java_jaxrsResponse.get();
                    if (JSON.validate(value)){
                        return JSON.decode(value);
                    }
                    return value;
                }
            }
        }
        if (globalThis.java_jaxwsResponse) {   //java_jaxwsResponse 객체 주입, 스크립트가 서비스 호출 활동, 응답 스크립트 에서 실행되며 호출 유형이 jaxws 임을 의미
            return {
                value: globalThis.java_jaxwsResponse,
                get(){
                    return globalThis.java_jaxwsResponse || null;
                }
            }
        }
        return null;
    }
}

/***************************************************************/
/* workContext -프로세스 스크립트의 workcontext 객체, 프로세스 인스턴스 관련 데이터 조회용  *****/
/**************************************************************/
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
const workContext = {
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
     * const task = this.workContext.getTask();
     */
    getTask: function(){  //work로 현재 처리 중인 task 또는 TaskCompleted json 문자열 조회. 진행 과정에서만 조회 가능
        var taskString = globalThis.java_workContext.getTaskOrTaskCompleted();
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
     * const work = this.workContext.getWork();
     */
    "getWork": function(){  //work 문자열
        var work = JSON.parse(globalThis.java_workContext.getWork());
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
     * const taskList = this.workContext.getTaskList();
     */
    "getTaskList": function(){return JSON.parse(globalThis.java_workContext.getTaskList());},                       //work로 모든 task 배열 json 문자열 조회

    /**
     * 현재 프로세스 인스턴스의 모든 처리 완료 객체 조회. 프로세스 인스턴스를 처리한 사람이 없으면 빈 배열 반환.
     * @method getTaskCompletedList
     * @methodOf module:server.workContext
     * @static
     * @return {(TaskCompleted[])} 처리 완료 작업 목록.
     * @o2ActionOut x_processplatform_assemble_surface.TaskCompletedAction.listWithWork|example=Task
     * @o2syntax
     * const taskCompletedList = this.workContext.getTaskCompletedList();
     */
    "getTaskCompletedList": function(){return JSON.parse(globalThis.java_workContext.getTaskCompletedList());},     //work로 모든 TaskCompleted 배열 json 문자열 조회

    /**
     * @summary 현재 프로세스 인스턴스의 모든 열람 대기 객체 배열을 조회합니다. 열람 대기가 없으면 빈 배열을 반환합니다.
     * @method getReadList
     * @methodOf module:server.workContext
     * @static
     * @return {(Read[])} 현재 프로세스 인스턴스의 모든 열람 대기 객체 배열.
     * @o2ActionOut x_processplatform_assemble_surface.ReadAction.get|example=Read
     * @o2syntax
     * const readList = this.workContext.getReadList();
     */
    "getReadList": function(){return JSON.parse(globalThis.java_workContext.getReadList());},                       //work로 모든 read 배열 json 문자열 조회

    /**
     * @summary 현재 프로세스 인스턴스의 모든 열람 완료 객체를 조회합니다. 열람 완료가 없으면 빈 배열을 반환합니다.
     * @method getReadCompletedList
     * @methodOf module:server.workContext
     * @static
     * @return {(ReadCompleted[])} 현재 프로세스 인스턴스의 모든 열람 완료 객체 배열.
     * @o2ActionOut x_processplatform_assemble_surface.ReadCompletedAction.listWithWork|example=Read
     * @o2syntax
     * const readCompletedList = this.workContext.getReadCompletedList();
     */
    "getReadCompletedList": function(){return JSON.parse(globalThis.java_workContext.getReadCompletedList());},     //work로 모든 ReadCompleted 배열 json 문자열 조회

    /**
     * @summary 현재 프로세스 인스턴스의 모든 review 객체 조회. review가 없으면 빈 배열 반환.
     * @method getReviewList
     * @alias getReviewListByJob
     * @methodOf module:server.workContext
     * @static
     * @return {(Review[])} 현재 프로세스 인스턴스의 모든 review 객체 배열.
     * @o2ActionOut x_processplatform_assemble_surface.ReviewAction.listWithJob|example=Review
     * @o2syntax
     * const reviewList = this.workContext.getReviewList();
     */
    "getReviewList": function(){return JSON.parse(globalThis.java_workContext.getJobReviewList());},

    /**
     * @summary getTaskListByJob 메서드의 별칭.
     * @method getJobTaskList
     * @static
     * @see server.module:workContext.getTaskListByJob
     */
    "getJobTaskList": function(){return JSON.parse(globalThis.java_workContext.getJobTaskList());},                 //jobid로 모든 task 배열 json 문자열 조회

    /**
     * @summary getTaskCompletedListByJob 메서드의 별칭.
     * @method getJobTaskCompletedList
     * @static
     * @see server.module:workContext.getTaskCompletedListByJob
     */
    "getJobTaskCompletedList": function(){return JSON.parse(globalThis.java_workContext.getJobTaskCompletedList());},   //jobid로 모든 TaskCompleted 배열 json 문자열 조회
    /**
     * @summary getReadListByJob 메서드의 별칭.
     * @method getJobReadList
     * @static
     * @see server.module:workContext.getReadListByJob
     */
    "getJobReadList": function(){return JSON.parse(globalThis.java_workContext.getJobReadList());},                     //jobid로 모든 read 배열 json 문자열 조회
    /**
     * @summary getReadCompletedListByJob 메서드의 별칭.
     * @method getJobReadCompletedList
     * @static
     * @see server.module:workContext.getReadCompletedListByJob
     */
    "getJobReadCompletedList": function(){return JSON.parse(globalThis.java_workContext.getJobReadCompletedList());},   //jobid로 모든 ReadCompleted 배열 json 문자열 조회
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
     * const taskList = this.workContext.getTaskListByJob();
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
     * const taskCompletedList = this.workContext.getTaskCompletedListByJob();
     */
    "getTaskCompletedListByJob": function(){return this.getJobTaskCompletedList();},
    /**
     * @summary 현재 업무 문서의 job으로 현재 프로세스 인스턴스의 모든 열람 대기 객체를 조회합니다. 열람 대기가 없으면 빈 배열을 반환합니다.
     * @method getReadListByJob
     * @methodOf module:server.workContext
     * @static
     * @return {(Read[])} 현재 프로세스 인스턴스의 모든 열람 대기 객체 배열.
     * @o2ActionOut x_processplatform_assemble_surface.ReadAction.listWithJob|example=Read
     * @o2syntax
     * const readList = this.workContext.getReadListByJob();
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
     * const readCompletedList = this.workContext.getReadCompletedListByJob();
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
     * const activity = this.workContext.getActivity();
     */
    "getActivity": function(){return JSON.parse(globalThis.java_workContext.getActivity());},       //활동 객체 문자열

    /**
     * @summary 현재 프로세스 인스턴스의 모든 프로세스 기록(WorkLog) 조회.
     * @method getWorkLogList
     * @static
     * @return {WorkLog[]} 프로세스 기록 객체.
     * @o2ActionOut x_processplatform_assemble_surface.WorkLogAction.listWithJob|example=WorkLog|ignoreProps=[properties,goBackFromActivityType]
     * @o2syntax
     * const workLogList = this.workContext.getWorkLogList();
     */
    "getWorkLogList": function(){return JSON.parse(globalThis.java_workContext.getWorkLogList());}, //WorkLogList 객체 배열 문자열

    /**
     * @summary 현재 프로세스 인스턴스의 모든 프로세스 기록(Record) 조회.
     * @method getRecordList
     * @o2ActionOut x_processplatform_assemble_surface.RecordAction.listWithJob|example=Record
     * @static
     * @return {Record[]} 프로세스 기록(Record) 객체.
     * @o2syntax
     * const recordList = this.workContext.getRecordList();
     */
    "getRecordList": function(){return JSON.parse(globalThis.java_workContext.getRecordList());}, //RecordList 객체 배열 문자열, 추가 필요

    /**
     * @summary 마지막 Record 객체. 활동 진행 완료 이벤트에서는 이번 진행의 record를, 그 밖의 이벤트에서는 job 전체의 마지막 record를 가져오며 이번 진행의 record가 아님.
     * @method getRecord
     * @o2ActionOut x_processplatform_assemble_surface.RecordAction.listWithJob|example=Record
     * @static
     * @return {Record[]} 프로세스 기록(Record) 객체.
     * @o2syntax
     * const record = this.workContext.getRecord();
     */
    "getRecord": function(){return JSON.parse(globalThis.java_workContext.getRecord());}, //마지막 Record 객체(활동 진행 완료 이벤트에서는 이번 진행의 record를, 그 밖의 이벤트에서는 job 전체의 마지막 record를 가져오며 이번 진행의 record가 아님)

    /**
     * @summary 현재 프로세스 인스턴스의 첨부파일 객체 목록 조회.
     * @method getAttachmentList
     * @static
     * @return {WorkAttachmentData[]} 첨부파일 데이터.
     * @o2ActionOut x_processplatform_assemble_surface.AttachmentAction.getWithWorkOrWorkCompleted|example=Attachment|ignoreProps=[properties]
     * @o2syntax
     * //첨부파일 목록 조회
     * const attachmentList = this.workContext.getAttachmentList();
     */
    "getAttachmentList": function(){return JSON.parse(globalThis.java_workContext.getAttachmentList());},   //첨부파일 객체 배열 문자열

    /**
     * @summary 선택 가능한 라우트 객체 배열 문자열 조회(진행 이벤트에서 현재 work 상태에 따라 진행 중 선택 가능한 라우트 목록을 가져옴).
     * @method getRouteList
     * @static
     * @return {Route[]} 경로 문자열 배열.
     * @o2syntax
     * const routeList = this.workContext.getRouteList();
     */
    "getRouteList": function(){return JSON.parse(globalThis.java_workContext.getRouteList());},      //선택 가능한 라우트 객체 배열 문자열(진행 이벤트에서 현재 work 상태에 따라 진행 중 선택 가능한 라우트 목록을 가져옴)

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
    "setTitle": function(title){globalThis.java_workContext.setTitle(title);},                       //title 설정


    "getControl": function(){return null;},
    "getInquiredRouteList": function(){return null;}
};
const workContent = workContext;

/***************************************************************/
/* assignData - 프로세스 호출 스크립트, 데이터 스크립트
{
	data:{},
	application:"",
	process:"",
	identity: "",
	attachmentList: [],
	title: "",
	processing: true
}
return 으로 assignData 반환 가능,  **********************************/
/**************************************************************/
/**
 * 프로세스 설정의 프로세스 호출 활동에서 “데이터 스크립트”와 “제목 스크립트”에 사용, assignData 객체로 호출할 프로세스의 관련 정보와 호출되는 프로세스 인스턴스에 전달할 업무 데이터를 조회할 수 있음.<br>
 * 업무 데이터를 수정한 뒤 assignData의 set 메서드로 호출되는 프로세스 인스턴스에 전달할 수도 있습니다.<br/>
 * @o2range 프로세스 설정-프로세스 호출 활동의 「데이터 스크립트」에서 사용 가능
 * @module server.assignData
 * @o2cn 프로세스 인스턴스 업무 데이터
 * @o2category server.process
 * @o2ordernumber 210
 * @example
 * //프로세스 호출 활동의 「데이터 스크립트」에서 아래 코드로 업무 데이터를 수정해 호출되는 프로세스 인스턴스에 전달합니다:
 * const data = this.assignData.get();
 * data.data.parentProcessData = "상위 프로세스 인스턴스 정보";
 * this.assignData.set(data);
 * @example
 * <caption>
 *    assignData.set 메서드는 이전 버전과의 호환을 위한 것입니다.<br>
 *    <b>json 객체를 return하는 방식으로 data 내용을 설정할 것을 권장합니다</b>
 * </caption>
 * //json 객체를 return하는 방식으로 assignData.set 메서드를 대체할 수도 있습니다
 * const data = this.assignData.get();
 * data.data.parentProcessData = "상위 프로세스 인스턴스 정보";
 * return data;
 */
const assignData = {     //java_assignData 앱 호출 활동에서 생성한 프로세스 인스턴스의 업무 데이터 처리 객체, get set 메서드
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
     * const data = this.assignData.get();
     */
    "get": function(){
        this.data = JSON.parse(globalThis.java_assignData.get());
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
     * const data = this.assignData.get();
     * data.data.parentProcessData = "상위 프로세스 인스턴스 정보";
     * return data;
     */
    "set": function(data){
        globalThis.java_assignData.set(JSON.stringify(data || this.data));
    }
};
Object.defineProperties(assignData, {"data": {
        "get": function(){
            if (this._data) return this._data;
            return JSON.parse(globalThis.java_assignData.get());
        },
        "set": function(v){this._data = v;}
    }}
);


/***************************************************************/
/* parameters - 서비스 호출 활동, 파라미터 스크립트, 전달할 파라미터 ******************/
/*              데이터 센터 쿼리문 스크립트에서 쿼리문 파라미터 조회.***************/
/*   - 호출 활동에서 webservice 호출이면 java_jaxwsParameters 객체를 주입하며, parameters 는 add 와 remove 메서드를 포함한 객체를 반환;***************/
/*   - 호출 활동에서 restful 호출이면 java_jaxrsParameters 객체를 주입하며, parameters 는 put 과 remove 메서드를 포함한 객체를 반환;******************/
/*   - 데이터 센터 쿼리문 스크립트에서는 java_parameters json 텍스트를 주입하며, parameters 는 쿼리문 호출 파라미터 조회용 json 객체를 반환******************/
/***************************************************************/
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
/**
 * 데이터 센터 쿼리 구문의 스크립트에서 구문 매개변수를 조회할 수 있음. json 객체이며 이 구문을 호출할 때 전달됨<br>
 * @o2range 데이터 센터-쿼리 설정-스크립트로 쿼리 구문 생성
 * @o2cn 데이터 센터 쿼리 구문 매개변수
 * @module server.service.parameters
 * @o2category server.service
 * @o2ordernumber 255
 * @o2syntax
 * var pars = this.parameters;
 * @example
 * <caption>
 *     this.statement.execute로 「task」라는 쿼리 설정을 호출하고 parameters를 전달하는 코드는 다음과 같음:
 *     <pre><code class='language-js'>//「task」라는 쿼리 설정을 호출하고 parameters 전달
 * this.statement.execute({
 *  "name": "task",
 *  "mode" : "all",
 *  "parameter" : {
 *     "person" : "xxx@xxx@p",  //사용자 매개변수 전달
 *     "startTime" : (new Date("2020-01-01")) //시간 매개변수 전달
 *  }
 * }, function(json){
 *  var count = json.count; //총수 문 실행 후 반환된 숫자
 *  var list = json.data; //쿼리 문 실행 후 반환된 배열
 *   //......
 * });
 *     </code></pre>
 *
 *     task 쿼리 설정의 스크립트에서 parameters 객체로 쿼리 실행 시 전달된 parameters 객체를 얻어 쿼리 구문을 동적으로 생성할 수 있음
 * </caption>
 * //지정한 사용자의 지정 시간 이후 모든 결재 대기 데이터를 조회하는 구문을 동적으로 생성
 * var user = this.parameters.person;
 * var startTime = (new Date(this.parameters.startTime)).format("db");  //yyyy-mm-dd hh:mm:ss 형식으로 변환
 * return "SELECT o FROM Task o WHERE o.person='"+user+"' AND o.startTime>{ts '"+startTime+"'}"
 */
const parametersDescriptor = {
    get: function(){
        if (globalThis.java_jaxwsParameters){   //java_jaxwsParameters 객체 주입, 스크립트가 서비스 호출 활동, 파라미터 스크립트 에서 실행되며 호출 유형이 jaxws 임을 의미
            return {
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
                add(value){
                    if (Array.isArray(value)){
                        value.forEach(function(v){
                            globalThis.java_jaxwsParameters.add(v);
                        });
                    }else{
                        globalThis.java_jaxwsParameters.add(value);
                    }
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
                remove(name){
                    globalThis.java_jaxwsParameters.remove(name);
                }
            }
        }
        if (globalThis.java_jaxrsParameters){   //java_jaxrsParameters 객체 주입, 스크립트가 서비스 호출 활동, 파라미터 스크립트 에서 실행되며 호출 유형이 jaxrs 임을 의미
            return {
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
                put(name, value){
                    if ((typeof name)==="object"){
                        var _keys = Object.keys(name);
                        for (var i=0; i<_keys.length; i++){
                           globalThis.java_jaxrsParameters.put(_keys[i], name[_keys[i]]);
                        }
                    }else{
                        globalThis.java_jaxrsParameters.put(name, value);
                    }
                },
                remove(name){
                    globalThis.java_jaxrsParameters.remove(name);
                }
            }
        }
        if (globalThis.java_parameters){        //java_parameters 객체 주입, 스크립트가 데이터 센터 쿼리문 스크립트 에서 실행됨을 의미
            return JSON.parse(globalThis.java_parameters)
        }
        return null;
    }
}


/***************************************************************/
/* body - 서비스 호출 활동, 메시지 본문 스크립트, 호출 메시지 본문 설정 ******************/
/***************************************************************/
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
const body = {
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
    set(data){
        if ((typeof data)==="string"){
            globalThis.java_jaxrsBody.set(data);
        }
        if ((typeof data)==="object"){
            globalThis.java_jaxrsBody.set(JSON.stringify(data));
        }
    }
};

/***************************************************************/
/* headers - 서비스 호출 활동, 메시지 헤더 스크립트, 호출 메시지 헤더 설정 ******************/
/***************************************************************/
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
const headers = {
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
    put(name, value){
        try{
            if ((typeof name)==="object"){
                var _keys = Object.keys(name);
                for (var i=0; i<_keys.length; i++){
                    if (globalThis.java_jaxrsHeaders) globalThis.java_jaxrsHeaders.put(_keys[i], name[_keys[i]]);
                }
            }else{
                if (globalThis.java_jaxrsHeaders) globalThis.java_jaxrsHeaders.put(name, value);
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
    remove(name){
        try{
            if (globalThis.java_jaxrsHeaders) globalThis.java_jaxrsHeaders.remove(name);
        }catch(e){}
    }
};

/***************************************************************/
/* requestText - 서비스 활동, 서비스 응답 스크립트, 요청 텍스트 내용 조회  ******/
/*               서비스 관리 invoke 요청 메시지 본문의 텍스트 내용 *     ******/
/***************************************************************/
/**
 * 인터페이스 호출 시 전달된 요청 메시지 본문의 텍스트 내용.
 * @o2range 서비스 관리-인터페이스
 * @module server.service.requestText
 * @o2cn 전달된 서비스 메시지 본문 텍스트
 * @o2category server.service
 * @o2ordernumber 250
 * @o2syntax
 * const text = this.requestText;
 * @example
 * const text = this.requestText; //전달된 서비스 메시지 본문 텍스트
 * const object = JSON.parse(text); //객체로 변환
 */
/**
 * 프로세스 설정의 서비스 활동에서 “서비스 응답 스크립트”에 사용, 요청 텍스트 내용 조회.<br>
 * @o2range 프로세스 설정-서비스 활동의 「응답 스크립트」
 * @module server.requestText
 * @o2cn 서비스 호출 요청 객체
 * @o2category server.process
 * @o2ordernumber 235
 * @o2syntax
 * const text = this.requestText;
 * const object = JSON.parse(text); //객체로 변환
 */
const requestTextDescriptor = {
    get: function(){
        return globalThis.java_requestText || '';
    }
}

/***************************************************************/
/* request - 서비스 활동, 서비스 응답 스크립트, 요청 내용 조회  ****************/
/*           서비스 관리 invoke 요청 java 객체 java_request *********/
/***************************************************************/
const requestDescriptor = {
    get: function(){
        if (globalThis.java_request){       //java_request 객체 주입, 스크립트가 서비스 관리 invoke 에서 실행됨을 의미. java_request 객체를 그대로 반환.
            /**
             * 인터페이스 호출 시 전달된 요청 객체. java의 request(java.net.http.HttpRequest) 객체.
             * @o2range 서비스 관리-인터페이스
             * @module server.service.request
             * @o2cn 전달된 서비스 메시지 본문 텍스트
             * @o2category server.service
             * @o2ordernumber 250
             * @o2syntax
             * const req = this.request;
             */
            globalThis.java_request.getBody = function(){
                try{
                    return JSON.parse(globalThis.java_requestText);
                }catch(e){
                    return globalThis.java_requestText;
                }
            }
            return globalThis.java_request;
        }
        if (globalThis.java_requestText) {  //java_requestText 객체 주입, 스크립트가 서비스 활동, 서비스 응답 스크립트 에서 실행됨을 의미.
            /**
             * 프로세스 설정의 서비스 활동 「서비스 응답 스크립트」에서 사용하며, 서비스를 요청한 요청 객체를 나타냅니다.<br>
             * @o2range 프로세스 설정-서비스 활동의 「응답 스크립트」
             * @module server.request
             * @o2cn 서비스 호출 요청 객체
             * @o2category server.process
             * @o2ordernumber 235
             * @o2syntax
             * const res = this.request;
             */
            return {
                /**
                 * @summary 요청 body 내용 조회.
                 * @method getBody
                 * @methodOf server.module:request
                 * @static
                 * @return {Object|String} 요청 body 내용. json으로 변환 가능하면 json 객체를, 아니면 요청 내용 텍스트를 반환.
                 * @o2syntax
                 * const req = this.request.getBody();
                 */
                getBody(){
                    try{
                        return JSON.parse(globalThis.java_requestText);
                    }catch(e){
                        return globalThis.java_requestText;
                    }
                },
                /**
                 * @summary 요청 body의 원본 내용 조회.
                 * @member {String} text
                 * @memberOf server.module:request
                 * @o2syntax
                 * var req = this.request.text;
                 */
                get text(){
                    return globalThis.java_requestText;
                }
            }
        }
        return null;
    }
}


/***************************************************************/
/* expire - 활동 기한 계산 객체, 활동 기한 설정용 ********************/
/***************************************************************/
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
const expire = {
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
        try{globalThis.java_expire.setHour(hour);}catch(e){}
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
        try{globalThis.java_expire.setWorkHour(hour);}catch(e){}
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
        try{globalThis.java_expire.setDate(date);}catch(e){}
    }
};



/******************************************/
/******************************************/
/*  data 와 embedData 처리 **************/
/******************************************/
/******************************************/
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

const ArrayList = Java.type('java.util.ArrayList');
const createProxy = function(target, j_data){
    target.$Jdata ?? Object.defineProperty(target, '$Jdata', {
        value: j_data
    });
    return new Proxy(target, {
        get(target, property){
            if (property==='add'){
                return function(key, value){
                    if (key==="length" && (target.$Jdata instanceof ArrayList)){
                        while (target.$Jdata.size()>value){
                            target.$Jdata.remove(target.$Jdata.size()-1);
                        }
                    }else{
                        target.$Jdata[key] = value;
                    }
                    return Reflect.set(target, key, value);
                }
            }
            if (property==='del'){
                return function (key) {
                    delete target.$Jdata[key];
                    delete target[key];
                }
            }
            return (property!=='$Jdata' && target[property] && (typeof target[property]==='object' || Array.isArray(target[property]))) ? createProxy(target[property], target.$Jdata[property]) : target[property];
        },
        set(target, property, value){
            if (property==="length" && (target.$Jdata instanceof ArrayList)){
                while (target.$Jdata.size()>value){
                    target.$Jdata.remove(target.$Jdata.size()-1);
                }
            }else{
                target.$Jdata[property] = value;
            }
            return Reflect.set(...arguments);
        }
    });
}

const _javaDataToJson = (javaData)=>{
    try{
        // const Gson = Java.type('com.google.gson.Gson');
        // const gson = new Gson();
        // return JSON.parse(gson.toJson(javaData));
        return JSON.parse(javaData.toString());
    }catch(e) {
        return null;
    }
}

let _data = null;
const dataDescriptor = {
    get(){
        return _data ?? (()=>{
            if (!globalThis.java_data) return null;
            const data = _javaDataToJson(globalThis.java_data);
            return _data = createProxy(data, globalThis.java_data);
        })();
    }
}

let _embedData = null;
const embedDataDescriptor = {
    get(){
        return _embedData ?? (()=>{
            if (!globalThis.java_embedData) return null;
            // const Gson = Java.type('com.google.gson.Gson');
            // const gson = new Gson();
            const data = _javaDataToJson(globalThis.java_embedData);
            return _embedData = createProxy(data, globalThis.java_embedData);
        })();
    }
}


/************************************************************/
/*  person text 직접 주입, oauth 설정과 기본 비밀번호 생성 스크립트에서 사용 *****/
/************************************************************/

/************************************************************/
/*  value 직접 주입, 뷰 표시 값에서 사용 ***************************/
/************************************************************/

const _get = function(key, java_data){
    if (!globalThis[`o${key}`]) globalThis[`o${key}`] = JSON.parse(java_data);
    return globalThis[`o${key}`];
}
const o= {
    entityManager: { get: function(){return null;} },
    context: { get: function(){return ((globalThis.java_resources) ? globalThis.java_resources.getContext() : null)} },
    applications: { get: function(){return ((globalThis.java_resources) ? globalThis.java_resources.getApplications() : null)} },
    organization: { get: function(){return ((globalThis.java_resources) ? globalThis.java_resources.getOrganization() : null)} },
    /**
     * 현재 사용자 객체 조회.
     * @module server.currentPerson
     * @o2cn 현재 사용자 전체 이름 객체
     * @o2category server.common
     * @o2ordernumber 250
     * @o2syntax
     * var user = this.currentPerson;
     */
    currentPerson: { get: function(){return (globalThis.java_effectivePerson) ? _get('currentPerson', globalThis.java_effectivePerson) : null;} },
    effectivePerson: { get: function(){return (globalThis.java_effectivePerson) ? _get('effectivePerson', globalThis.java_effectivePerson) : null;} },

    resources: { get: function(){return (globalThis.java_resources || null)} },
    customResponse: { get: function(){return (globalThis.java_customResponse || null)} },
    message: { get: function(){return (globalThis.java_message) ? _get('message', globalThis.java_message) : null;} },

    //===========================================================================
    //  내보낼 객체와 메서드를 globalThis 에 바인딩.(향후 export 와 import 로 가져올 예정)
    //===========================================================================
    fetch: { value: fetch },
    exec: { value: exec },
    define: { value: define },
    Action: { value: Action },
    log: { value: log },
    Actions: { value: Actions },
    print: { value: print },
    org: { value: org },
    processActions: { value: processActions },
    cmsActions: { value: cmsActions },
    portalActions: { value: portalActions },
    serviceActions: { value: serviceActions },
    includedScripts: { value: includedScripts },
    include: { value: include },
    includeHtml: { value: includeHtml },
    includeCss: { value: includeCss },
    includeJson: { value: includeJson },
    Dict: { value: Dict },
    Table: { value: Table },
    statement: { value: statement },
    view: { value: view },
    service: { value: service },

    response: responseDescriptor,
    parameters: parametersDescriptor,
    requestText: requestTextDescriptor,
    request: requestDescriptor,

    workContext: { value: workContext },
    workContent: { value: workContext },
    assignData: { value: assignData },
    body: { value: body },
    headers: { value: headers },
    expire: { value: expire },

    // @todo javascript 객체로 래핑 필요
    data: dataDescriptor,
    embedData: embedDataDescriptor
    // embedData: { value: globalThis.embedData }
};

Object.defineProperties(globalThis, o);

/* engine 정리 */
globalThis.engine = null;


//=== java_resources: =============
//모든 서버 스크립트에 있는 java 객체
//  Context                 getContext()                    앱 컨텍스트 객체, 사용 안 함
//  Applications            getApplications()               서버와 통신하는 요청 메서드
//          describeApi()
//          getQuery()
//          postQuery()
//          putQuery()
//          deleteQuery()

//  Organization            getOrganization()               조직 관련 서비스
//  WebservicesClient       getWebservicesClient()          HTTP 요청과 Webservice 요청 실행 도구
//  EntityManagerContainer  getEntityManagerContainer()     jpa 관리 객체, 노출 안 함

//=== java_customResponse: =========================================
// 서비스 관리 invoke 에서만 사용, 주로 invoke 의 요청 응답 java 객체 설정용
//      seeOther()              303 리다이렉트 설정
//      temporaryRedirect()     301 리다이렉트 설정
//      setBody()               응답 데이터와 응답 content-type 200 설정

//=== java_effectivePerson: =========================================
//현재 사용자 객체 java 객체 -- json 문자열로 변경 todo
//      toJson() json 문자열로 변환

//=== java_parameters: ==============================================
//쿼리문에서만 사용, 파라미터 전달용 json 문자열

//=== java_requestText: ==============================================
// 서비스 관리 invoke 에서만 사용, 요청 메시지 본문 텍스트 내용 조회 json 문자열

//=== java_request: ==================================================
// 서비스 관리 invoke 에서만 사용, 요청 request 객체 java 객체

//=== java_message: ==================================================
// 메시지 필터와 로더에서만 사용, 메시지를 설명하는 json 문자열
// 주요 정보: title, person, body, type

//=== person: ========================================================
//  시스템 설정의 기본 사용자 비밀번호 설정 스크립트에서만 사용, 현재 실행 중인 비밀번호를 생성할 사용자 조회. java 객체
//  getName(), getMobile(), getEmployee() 등 개인 정보 조회 메서드
