# O2OA Web

O2 플랫폼 웹 앱.

[![Build Status](https://travis-ci.com/huqi1980/o2oa_client_web.svg?branch=master)](https://travis-ci.org/o2oa/o2oa)
[![AGPL](https://img.shields.io/badge/license-AGPL-blue.svg)](https://github.com/o2oa/o2oa)
[![code-size](https://img.shields.io/github/languages/code-size/o2oa/o2oa.svg)](https://github.com/o2oa/o2oa)
[![last-commit](https://img.shields.io/github/last-commit/o2oa/o2oa.svg)](https://github.com/o2oa/o2oa)
---

## 소개

O2 플랫폼 웹 앱으로, o2server와 함께 컴파일됩니다. 따로 컴파일해 임의의 웹 서버에 배포할 수도 있습니다.

## 컴파일

먼저 npm 환경을 설치합니다

    $ npm install
    $ npm i -g gulp-cli
   

다음 명령으로 컴파일합니다:

    $ gulp

다음 매개변수를 사용하면 컴파일 후 FTP로 웹 서버에 바로 배포할 수 있습니다:

	$ gulp --upload ftp --host ftp.server.com --user ftpuser --pass password --port 21 --remotePath /


> --upload : 선택 값 `local`, `ftp`, `sftp`
>
> --host : ftp 또는 sftp 서버
>
> --user : 사용자 이름, 기본값 `anonymous`
>
> --pass : 비밀번호, ftp 또는 sftp 비밀번호, 기본값 `@anonymous`
>
> --port : 포트, ftp 기본값 21, sftp 기본값 22
>
> --remotePath: 원격 배포 경로, 기본값 “/”

## 설정

o2web을 다른 웹 서버에 배포했다면 아래 config.jon 파일을 직접 수정해야 합니다.

경로: /x_desktop/res/config/config.json

	{
	  "center": [		//여러 중앙 서버 주소를 설정할 수 있으며 시스템이 사용 가능한 서버를 자동으로 찾음
	    {
	      "port": "20030",	//중앙 서버 포트
	      "host": ""		//중앙 서버 host
	    },
	    {
	      "port": "20030",		//중앙 서버 포트
	      "host": "127.0.0.1"	//중앙 서버 host
	    }
	  ],
	  "footer": "개발 시스템",		//시스템 바닥글
	  "title": "o2oa 개발 플랫폼",	//시스템 이름
	  "app_protocol": "auto",	//http, https 또는 auto
	  "loginPage": {			//portal 페이지를 로그인 페이지로 사용
	    "enable": false,
	    "portal": "",
	    "page": ""
	  }
	}

## 공식 웹사이트\:

공식 웹사이트 : [http://www.o2oa.net](http://www.o2oa.net)

oschina 프로젝트 홈 : [https://www.oschina.net/p/o2oa](https://www.oschina.net/p/o2oa)

다운로드 주소 : [http://www.o2oa.net](http://www.o2oa.net/download.html)


