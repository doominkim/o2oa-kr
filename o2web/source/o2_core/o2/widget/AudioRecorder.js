o2.widget = o2.widget || {};
o2.require("o2.widget.Common", null, false);
o2.widget.AudioRecorder = new Class({
	Implements: [Options, Events],
	Extends: o2.widget.Common,
	options: {
		"style": "default",
        "config": {
		    "sampleBits": 8,        //샘플링 비트 수 8, 16
            "sampleRate": 44100/6,  //샘플링 레이트(1/6 44100)
            "inputChannels": 1,     //입력 채널 수 1,2
            "outputChannels": 1     //출력 채널 수 1,2
        },
        "visible": true,
        "visualize": true  //시각 효과
	},
	initialize: function(content, options){
		this.setOptions(options);
		this.content = content;
		if (this.options.visible){
            this.path = o2.session.path+"/widget/$AudioRecorder/";
            this.cssPath = o2.session.path+"/widget/$AudioRecorder/"+this.options.style+"/css.wcss";
            this._loadCss();
        }
        COMMON.AjaxModule.loadDom("../o2_lib/adapter/adapter.js", function(){
            this.load();
        }.bind(this));
	},
    load: function(){
        if (this.options.visible && this.content){
            this.createLayout();
            this.createToolbar();
        }
    },
    createLayout:function(){
        this.node = new Element("div", {"styles": this.css.node}).inject(this.content);
        this.actionNode = new Element("div", {"styles": this.css.actionNode}).inject(this.node);

        if (this.options.visualize){
            this.visualizeNode = new Element("div", {"styles": this.css.visualizeNode}).inject(this.node);
            var size = this.node.getSize();
            var actionSize = this.actionNode.getSize();
            var h = size.y-actionSize.y;
            //var pd = (h-100)/2;
            this.visualizeNode.setStyles({
                "height": ""+h+"px"
                // "padding-top": ""+pd+"px",
                // "padding-bottom": ""+pd+"px"
            });
            this.canvas = new Element('canvas', {"width": size.x, "height": h}).inject(this.visualizeNode);
            this.canvasCtx = this.canvas.getContext('2d');
        }
    },
    createToolbar: function(){
        o2.require("o2.widget.Toolbar", function(){

            this.toolbar = new o2.widget.Toolbar(this.actionNode, {"style": "audio"}, this);

            new Element("div", {
                "MWFnodeid": "record",
                "MWFnodetype": "MWFToolBarOnOffButton",
                "MWFButtonImage": this.path+""+this.options.style+"/icon/record.png",
                "title": o2.LP.widget.record,
                "MWFButtonAction": "recordAction",
                "MWFButtonText": o2.LP.widget.record
            }).inject(this.actionNode);

            new Element("div", {
                "MWFnodeid": "stop",
                "MWFnodetype": "MWFToolBarButton",
                "MWFButtonDisable": "yes",
                "MWFButtonImage": this.path+""+this.options.style+"/icon/stop.png",
                "title": o2.LP.widget.stop,
                "MWFButtonAction": "stopAction",
                "MWFButtonText": o2.LP.widget.stop
            }).inject(this.actionNode);

            new Element("div", {
                "MWFnodeid": "",
                "MWFnodetype": "MWFToolBarSeparator"
            }).inject(this.actionNode);

            new Element("div", {
                "MWFnodeid": "play",
                "MWFnodetype": "MWFToolBarButton",
                "MWFButtonDisable": "yes",
                "MWFButtonImage": this.path+""+this.options.style+"/icon/play.png",
                "title": o2.LP.widget.play,
                "MWFButtonAction": "playAction",
                "MWFButtonText": o2.LP.widget.play
            }).inject(this.actionNode);

            new Element("div", {
                "MWFnodeid": "save",
                "MWFnodetype": "MWFToolBarButton",
                "MWFButtonDisable": "yes",
                "MWFButtonImage": this.path+""+this.options.style+"/icon/save.png",
                "title": o2.LP.widget.save,
                "MWFButtonAction": "saveAction",
                "MWFButtonText": o2.LP.widget.save
            }).inject(this.actionNode);

            new Element("div", {
                "MWFnodeid": "cancel",
                "MWFnodetype": "MWFToolBarButton",
                "MWFButtonImage": this.path+""+this.options.style+"/icon/cancel.png",
                "title": o2.LP.widget.cancel,
                "MWFButtonAction": "cancelAction",
                "MWFButtonText": o2.LP.widget.cancel
            }).inject(this.actionNode);

            this.toolbar.load();
        }.bind(this))
    },
    saveAction: function(){
        var data = this.audioData.encodeWAV();
        this.fireEvent("save", [data]);
        this.close();
    },
    cancelAction: function(){
        this.fireEvent("cancel");
        this.close();
    },
    close: function(){
        if (this.context) this.context.close();
        if (this.node) this.node.destroy();
        o2.release(this);
    },
    recordAction: function(status, btn){
        if (btn.status==="on"){
            this.toolbar.items["stop"].enable();
            this.toolbar.items["play"].disable();
            this.toolbar.items["save"].disable();
            this.toolbar.items["cancel"].disable();
            this.startRecord();
        }else{
            btn.on();
        }
    },
    stopAction: function(){
        var record = this.toolbar.items["record"];
        if (record.status==="on"){
            record.off();
            this.stopRecord();

            if (this.audioData) {
                if (this.audioData.buffer.length){
                    this.toolbar.items["play"].enable();
                }
            }
        }
        this.toolbar.items["save"].enable();
        this.toolbar.items["cancel"].enable();
        this.toolbar.items["stop"].disable();
    },
    playAction: function(){
        var record = this.toolbar.items["record"];
        record.off();
        record.enable(false);
        this.toolbar.items["stop"].enable();
        this.toolbar.items["save"].disable();
        this.toolbar.items["cancel"].disable();

        if (!this.audioNode) this.audioNode = new Element("audio", {"loop": false});
        this.audioNode.set("src", window.URL.createObjectURL(this.audioData.encodeWAV()));
        this.audioNode.addEventListener("ended", function(){
            this.toolbar.items["save"].enable();
            this.toolbar.items["cancel"].enable();
        }.bind(this), true);
        this.audioNode.play();
    },
    stopRecord: function(){
        this.audioInput.disconnect();
        //this.volume.disconnect();
        if (this.canvas){
            this.canvasCtx.clearRect(0, 0, this.canvas.width, this.canvas.height);
            if (this.drawVisual) window.cancelAnimationFrame(this.drawVisual);
            if (this.audioNode){
                if (!this.audioNode.paused) this.audioNode.pause();
            }
        }
        this.context.suspend();
    },
    startRecord: function(){
        this.loadAudio(function(stream){
            this.initAudio(stream);
            this.initAudioData();
            this.startAudio();
            this.visualize(stream);
        }.bind(this));
    },
    startAudio: function () {
        if (this.context.state==="suspended") this.context.resume();
        if (this.audioData){
            this.audioData.size = 0;
            this.audioData.buffer = [];
        }

        this.audioInput.connect(this.analyser);
        this.analyser.connect(this.recorder);
        this.recorder.connect(this.volume);
        this.volume.connect(this.context.destination);

        this.volume.gain.value = 0;

        //this.volume.connect(this.recorder);
        //if (this.analyser) this.volume.connect(this.analyser);
       // this.recorder.connect(this.context.destination);
        //this.recorder.connect(this.analyser);
    },
    initAudioData: function(){
        if (!this.audioData){
            var _self = this;
            this.audioData = {
                size: 0,         //녹음 파일 길이
                buffer: [],     //녹음 버퍼
                inputSampleRate: _self.context.sampleRate,    //입력 샘플링 레이트
                inputSampleBits: 16,        //입력 샘플링 비트 수 8, 16
                outputSampleRate: _self.options.config.sampleRate,    //출력 샘플링 레이트
                outputSampleBits: _self.options.config.sampleBits,       //출력 샘플링 비트 수 8, 16
                input: function (data) {
                    this.buffer.push(new Float32Array(data));
                    this.size += data.length;
                },
                compress: function () { //병합 압축
                    //병합
                    var data = new Float32Array(this.size);
                    var offset = 0;
                    for (var i = 0; i < this.buffer.length; i++) {
                        data.set(this.buffer[i], offset);
                        offset += this.buffer[i].length;
                    }
                    //압축
                    var compression = parseInt(this.inputSampleRate / this.outputSampleRate);
                    var length = data.length / compression;
                    var result = new Float32Array(length);
                    var index = 0, j = 0;
                    while (index < length) {
                        result[index] = data[j];
                        j += compression;
                        index++;
                    }
                    return result;
                },
                encodeWAV: function () {
                    var sampleRate = Math.min(this.inputSampleRate, this.outputSampleRate);
                    var sampleBits = Math.min(this.inputSampleBits, this.outputSampleBits);
                    var bytes = this.compress();
                    var dataLength = bytes.length * (sampleBits / 8);
                    var buffer = new ArrayBuffer(44 + dataLength);
                    var data = new DataView(buffer);

                    var channelCount = 1;//모노
                    var offset = 0;

                    var writeString = function (str) {
                        for (var i = 0; i < str.length; i++) {
                            data.setUint8(offset + i, str.charCodeAt(i));
                        }
                    };

                    // 리소스 교환 파일 식별자
                    writeString('RIFF'); offset += 4;
                    // 다음 주소부터 파일 끝까지 총 바이트 수, 즉 파일 크기-8
                    data.setUint32(offset, 36 + dataLength, true); offset += 4;
                    // WAV 파일 표지
                    writeString('WAVE'); offset += 4;
                    // 파형 포맷 표지
                    writeString('fmt '); offset += 4;
                    // 필터 바이트, 일반적으로 0x10 = 16
                    data.setUint32(offset, 16, true); offset += 4;
                    // 포맷 종류 (PCM 형식 샘플 데이터)
                    data.setUint16(offset, 1, true); offset += 2;
                    // 채널 수
                    data.setUint16(offset, channelCount, true); offset += 2;
                    // 샘플링 레이트, 초당 샘플 수, 각 채널의 재생 속도
                    data.setUint32(offset, sampleRate, true); offset += 4;
                    // 파형 데이터 전송률 (초당 평균 바이트 수) 모노×초당 데이터 비트 수×샘플당 데이터 비트/8
                    data.setUint32(offset, channelCount * sampleRate * (sampleBits / 8), true); offset += 4;
                    // 블록 데이터 정렬 수 샘플 1회당 바이트 수 모노×샘플당 데이터 비트 수/8
                    data.setUint16(offset, channelCount * (sampleBits / 8), true); offset += 2;
                    // 샘플당 데이터 비트 수
                    data.setUint16(offset, sampleBits, true); offset += 2;
                    // 데이터 식별자
                    writeString('data'); offset += 4;
                    // 샘플 데이터 총수, 즉 데이터 총 크기-44
                    data.setUint32(offset, dataLength, true); offset += 4;
                    // 샘플 데이터 쓰기
                    if (sampleBits === 8) {
                        for (var i = 0; i < bytes.length; i++, offset++) {
                            var s = Math.max(-1, Math.min(1, bytes[i]));
                            var val = s < 0 ? s * 0x8000 : s * 0x7FFF;
                            val = parseInt(255 / (65535 / (val + 32768)));
                            data.setInt8(offset, val, true);
                        }
                    } else {
                        for (var i = 0; i < bytes.length; i++, offset += 2) {
                            var s = Math.max(-1, Math.min(1, bytes[i]));
                            data.setInt16(offset, s < 0 ? s * 0x8000 : s * 0x7FFF, true);
                        }
                    }

                    return new Blob([data], { type: 'audio/wav' });
                }
            }
        }
    },
    initAudio: function(stream){
        if (!this.context){
            //오디오 컨텍스트 객체 생성
            this.context = new (window.AudioContext || window.webkitAudioContext)();
            //소리를 이 객체에 입력
            this.audioInput = this.context.createMediaStreamSource(stream);

            //볼륨 노드 설정
            this.volume = this.context.createGain();

            //소리를 버퍼링할 버퍼 생성
            var bufferSize = 4096;

            // 소리 버퍼 노드 생성. createScriptProcessor 메서드의
            // 두 번째와 세 번째 인자는 입력과 출력이 모두 스테레오임을 의미함.
            this.recorder = this.context.createScriptProcessor(bufferSize, this.options.config.inputChannels, this.options.config.outputChannels);

            //if (this.visualizeNode){
                this.analyser = this.context.createAnalyser();
                //this.audioInput.connect(this.analyser);
            //}

            this.recorder.onaudioprocess = function (e) {
                this.recordAudioData(e.inputBuffer.getChannelData(0));
            }.bind(this);
        }
    },
    recordAudioData: function(data){
        this.audioData.input(data);
    },
    loadAudio: function(callback){
        if (navigator.getUserMedia) {
            navigator.getUserMedia(
                { audio: true } //오디오만 사용
                , function (stream) {
                    if (callback) callback(stream);
                }
                , function (error) {
                    switch (error.code || error.name) {
                        case 'PERMISSION_DENIED':
                        case 'PermissionDeniedError':
                            o2.xDesktop.notice("error", {"x": "right", "y": "top"}, o2.LP.widget.userRefuse, this.node);
                            //this.throwError('사용자가 정보 제공을 거부했습니다.');
                            break;
                        case 'NOT_SUPPORTED_ERROR':
                        case 'NotSupportedError':
                            o2.xDesktop.notice("error", {"x": "right", "y": "top"}, o2.LP.widget.explorerNotSupportDevice, this.node);
                            //this.throwError('<a href="http://www.it165.net/edu/ewl/" target="_blank" class="keylink">브라우저</a>가 하드웨어 장치를 지원하지 않습니다.');
                            break;
                        case 'MANDATORY_UNSATISFIED_ERROR':
                        case 'MandatoryUnsatisfiedError':
                            o2.xDesktop.notice("error", {"x": "right", "y": "top"}, o2.LP.widget.canNotFindDevice, this.node);
                            //this.throwError('지정한 하드웨어 장치를 찾을 수 없습니다.');
                            break;
                        default:
                            o2.xDesktop.notice("error", {"x": "right", "y": "top"}, o2.LP.widget.canNotOpenMicrophone + (error.code || error.name), this.node);
                            //this.throwError('마이크를 열 수 없습니다. 오류 정보:' + (error.code || error.name));
                            break;
                    }
                }.bind(this));
        } else {
            o2.xDesktop.notice("error", {"x": "right", "y": "top"}, o2.LP.widget.explorerNotSupportRecordVoice, this.node);
            //this.throwError('현재 <a href="http://www.it165.net/edu/ewl/" target="_blank" class="keylink">브라우저</a>가 녹음 기능을 지원하지 않습니다.'); return;
        }
    },
    // throwError: function (message) {
    //     throw new function () { this.toString = function () { return message; };};
    // },
    visualize: function (stream) {
        //sinewave
        var WIDTH = this.canvas.width;
        var HEIGHT = this.canvas.height;

        this.analyser.fftSize = 1024;
        var bufferLength = this.analyser.frequencyBinCount; // half the FFT value
        var dataArray = new Uint8Array(bufferLength); // create an array to store the data

        this.canvasCtx.clearRect(0, 0, WIDTH, HEIGHT);

        var _self = this;
        function draw() {

            _self.drawVisual = requestAnimationFrame(draw);

            _self.analyser.getByteTimeDomainData(dataArray); // get waveform data and put it into the array created above

            _self.canvasCtx.fillStyle = 'rgb(204, 204, 204)'; // draw wave with canvas
            _self.canvasCtx.fillRect(0, 0, WIDTH, HEIGHT);

            _self.canvasCtx.lineWidth = 1;
            _self.canvasCtx.strokeStyle = 'rgb(0, 0, 0)';

            _self.canvasCtx.beginPath();

            var sliceWidth = WIDTH * 1.0 / bufferLength;
            var x = 0;

            for(var i = 0; i < bufferLength; i++) {

                var v = dataArray[i] / 128.0;
                v = v-1;
                var y = v*30 + HEIGHT/2;

                if(i === 0) {
                    _self.canvasCtx.moveTo(x, y);
                } else {
                    _self.canvasCtx.lineTo(x, y);
                }

                x += sliceWidth;
            }

            _self.canvasCtx.lineTo(_self.canvas.width, _self.canvas.height/2);
            _self.canvasCtx.stroke();
        };
        draw();
    }
});
