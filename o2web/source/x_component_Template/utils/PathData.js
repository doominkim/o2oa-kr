MWF.PathDataHandler = new Class({
    Implements: [Options, Events],
    options: {
        'type': 'cms' //'job', 'work', 'workCompleted', 'cms'
    },
    initialize: function (type) {
        this.setType(type);
    },
    setType: function(type){
        this.options.type = type;

        this.action = this.options.type === 'cms' ?
            o2.Actions.load('x_cms_assemble_control').DataAction :
            o2.Actions.load('x_processplatform_assemble_surface').DataAction;


        var isCMS = this.options.type === 'cms';

        var processKey = isCMS ? '' : (this.options.type.charAt(0).toUpperCase() + this.options.type.slice(1));

        this.methodMap = {
            create: isCMS ? 'createWithDocument' : `createWith${processKey}`,
            update: isCMS ? 'updateWithDocument' : `updateWith${processKey}`,
            get: isCMS ? 'getWithDocument' : `getWith${processKey}`,
            delete: isCMS ? 'deleteWithDocument' : `deleteWith${processKey}`
        };
    },
    get: function(id, pathList, ...args){
        return this._execute('get', id, pathList, ...args);
    },
    create: function(id, pathList, data, ...args){
        if (data === undefined) {
            throw new Error('생성 작업에는 data 매개변수가 필요합니다');
        }
        return this._execute('create', id, pathList, data, ...args);
    },
    update: function(id, pathList, data, ...args){
        if (data === undefined) {
            throw new Error('업데이트 작업에는 data 매개변수가 필요합니다');
        }
        return this._execute('update', id, pathList, data, ...args);
    },
    delete: function(id, pathList, ...args){
        return this._execute('delete', id, pathList, ...args);
    },
    _safeDynamicCall: function(methodName, args){ // 비공개 메서드: 동적 호출 안전 래퍼
        if (typeof this.action[methodName] !== 'function') {
            var availableMethods = Object.keys(this.action).filter(k => typeof this.action[k] === 'function');
            throw new Error(`호출한 메서드가 없습니다: ${methodName} (사용 가능한 메서드: ${availableMethods.join(', ')})`);
        }
        try {
            return this.action[methodName](...args);
        } catch (e) {
            throw new Error(`${methodName} 호출 실패: ${e.message}`);
        }
    },
    // 비공개 메서드: 인수 검증
    _validateParameters: function (type, id, paths){
        if (typeof id !== 'string' || !id.trim()) {
            throw new Error('문서 ID는 유효한 문자열이어야 합니다');
        }

        if (paths.some(p => typeof p !== 'string' && typeof p !== 'number')) {
            throw new Error('경로 요소는 문자열 또는 숫자여야 합니다');
        }
    },
    _execute: function (type, id, paths = [], ...args){
        try {
            if( typeof paths === "string" ){
                paths = [paths];
            }
            // 1. 기본 검증
            this._validateParameters(type, id, paths);

            // 2. 인수 표준화
            paths = paths.map(p => p.toString()); // 모든 경로 요소가 문자열인지 보장

            // 3. 경로 깊이 검증
            if (paths.length > 8) {
                throw new Error(`경로 단계가 제한(8단계)을 초과했습니다. 현재: ${paths.length}`);
            }


            var baseMethod = this.methodMap[type];
            if (!baseMethod) {
                throw new Error(`지원하지 않는 작업 유형: ${type} (지원: ${Object.keys(this.methodMap).join(', ')})`);
            }

            // 6. 동적 메서드 처리
            var hasPath = paths.length > 0;

            var pathMethod = this.options.type === 'cms' ?
                `${baseMethod}${(paths.length===1 && type==='get')?'':'With'}Path${paths.length-1}` :
                `${baseMethod}Path${paths.length-1}`;

            var methodName = hasPath ? pathMethod : baseMethod;

            // 7. 호출 인수 준비
            var callArgs = [id];
            if (hasPath) callArgs.push(...paths);
            callArgs.push(...args);

            // 8. 안전 호출
            return this._safeDynamicCall(methodName, callArgs);

        } catch (error) {
            console.error(`PathData 작업 실패[${type}]`, {
                documentId: id,
                paths: paths,
                error: {
                    name: error.name,
                    message: error.message,
                    stack: error.stack
                }
            });
            throw error; // 호출 스택 유지를 위해 다시 throw
        }
    }
});
