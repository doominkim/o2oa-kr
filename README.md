<p align="center">
	<a target="_blank" href="https://github.com/o2oa/o2oa/blob/develop/LICENSE"><img alt="GitHub license" src="https://img.shields.io/github/license/o2oa/o2oa"></a>
	<a target="_blank" href="https://www.oracle.com/technetwork/java/javase/downloads/index.html">
		<img src="https://img.shields.io/badge/JDK-11-green" />
	</a>
	<a target="_blank" href="https://gitee.com/o2oa/O2OA/stargazers">
		<img src="https://gitee.com/o2oa/O2OA/badge/star.svg?theme=dark" alt='gitee star'/>
	</a>
	<a target="_blank" href="https://github.com/o2oa/o2oa/stargazers"><img alt="GitHub stars" src="https://img.shields.io/github/stars/o2oa/o2oa?style=social"></a>
</p>


----------------------------------------------------------------------------

# O2OA(아오투) 개발 플랫폼 (100% 오픈소스 OA 및 협업 오피스 솔루션)

O2OA(아오투) 로우코드 개발 플랫폼은 100% 오픈소스 기업 협업 오피스 맞춤형 플랫폼으로, 완전한 프런트엔드·백엔드 API와 모듈 맞춤 기능을 제공합니다. JavaEE 분산 아키텍처 기반으로 프로세스 엔진, 양식 맞춤, 페이지 맞춤 및 업무 데이터 서비스 기능을 갖추고, 크로스 플랫폼 모바일 오피스를 지원해 업무 효율을 효과적으로 높입니다.

플랫폼은 중국 국산화(신창) 인증을 통과했고, 화이트 라벨 2차 개발을 지원하며, 매우 유연한 협업 오피스 솔루션으로 적은 비용으로 기업의 업무 요구를 충족합니다. 로우코드 개발로 기술 장벽을 크게 낮추고 시스템 구축과 맞춤을 가속해 기업이 업무에 빠르게 대응하도록 하며, 정보 보안과 자주적 통제를 보장합니다.

![o2oa](https://www.o2oa.net/v3/img/home/pic_zonghe@2x.png)



# 공식 웹사이트:

오픈소스 홈페이지 : https://www.oschina.net/p/o2oa

공식 웹사이트 : http://www.o2oa.net

공식 포럼 : https://forum.o2oa.net/


# 운영 환경 데이터 보안 관련 권장 사항:

O2OA에 내장된 H2 데이터베이스는 임베디드 인메모리 데이터베이스로 개발 환경과 기능 시연 환경에 적합하며 운영 환경에는 적합하지 않습니다.

운영 환경으로 사용한다면 더 높은 성능과 안정성을 갖춘 상용 데이터베이스(Mysql8, Oracle12C, SQLServer 2012 등)를 사용하는 것을 권장합니다.

또한 O2OA는 데이터 정기 백업 및 복구 기능을 제공하므로, 데이터베이스 이상 시 데이터를 복구할 수 있도록 운영 환경의 정기 백업 기능을 켜 두는 것을 권장합니다.



# 지원 운영체제:

```shell
Windows 64Bit, Linux 64Bit[CentOS, RedHat, Ubuntu 등], MacOS, AIX, Raspberrypi(라즈베리 파이), ARM_Linux, MIPS_Linux, UOS, 기린 등 중국 국산 운영체제
```



# 플랫폼 사용 설명서:

https://www.o2oa.net/handbook.html



# 소스 컴파일 가이드:

https://www.o2oa.net/cms/source/335.html



# 서버 배포 가이드:

Windows 환경: https://www.o2oa.net/cms/serverdeployment/694.html

Linux 환경: https://www.o2oa.net/cms/serverdeployment/468.html



# How to Start

## windows

1.o2server. yyyyMMddHHmmss_ windows.zip 패키지를 내려받습니다.

2.내려받은 압축 파일을 임의의 디렉터리에 풉니다.

3.서버의 80, 20020, 20030 포트가 열려 있는지 확인합니다.

4.o2server 폴더를 열고 start_ windows.bat을 더블 클릭해 실행합니다.

5.서비스를 시작하고 관련 서비스가 자동으로 완료될 때까지 기다립니다.

6.자동 완료 후 브라우저에서 http://127.0.0.1 에 접속합니다.


## linux

1.o2server. yyyyMMddHHmmss_linux.zip 패키지를 내려받습니다.

2.서버의 80, 20020, 20030 포트가 열려 있는지 확인합니다.

3."unzip o2server. syyyMMddHHmmss linux.zip" 으로 패키지의 압축을 풉니다.

4."cd o2server. yyyMMddHHmmss_ linux" 로 압축 해제 디렉터리에 들어갑니다.

5."cd o2server" 로 프로그램 디렉터리에 들어갑니다.

6."./start. linux.sh" 입력 후 엔터를 눌러 서버 콘솔을 시작합니다.

7.서비스를 시작하고 관련 서비스가 자동으로 완료될 때까지 기다립니다.

8.자동 완료 후 브라우저에서 http://127.0.0.1 에 접속합니다.



#### 개발자가 O2OA를 학습·연구하거나, 기업이 O2OA 앱 개발 플랫폼에서 내부용 업무 시스템을 구축하면서 비공개 배포를 하지 않고 상업 프로젝트에 사용하지 않는 경우에는 권리 침해 위험이 없습니다.

#### 재판매, 비공개 배포 또는 상업 프로젝트의 일부로 사용하려면 란더 네트워크에 연락해 상용 라이선스를 구매하십시오.

상용 라이선스 및 지원 서비스: https://www.o2oa.net/service/gs.html



# 라이선스

[AGPL-3.0 오픈소스 라이선스.](./LICENSE)



# 소개

[![img](https://www.o2oa.net/v3/img/common/logo_all@2x.png)](./assets/O2OA-logo.jpg)

저장 란더쭝헝 네트워크 기술 주식회사는 1999년에 설립되었으며, 저장성 첨단기술기업이자 저장성 정보산업청이 인정한 소프트웨어 기업, 저장성 정보산업 중점기업, 저장성 소프트웨어업계 5대 기업입니다. 본사는 5,300여 제곱미터의 사무 공간을 보유하고 있고 직원 수는 600명을 넘으며, 베이징, 상하이, 선전, 톈진, 난징, 허페이, 정저우, 충칭, 선양, 창춘, 하얼빈, 후허하오터, 지난, 난창 등 성도급 도시에 사무소를 두고 있어 서비스 네트워크가 중국 및 동남아 일부 국가와 지역을 포괄합니다. 회사는 ISO9001:2008 국제 품질 시스템 인증을 받았으며, 국가 시스템 통합 3급 자격 인증과 CMI 3급 인증서를 취득했습니다.

란더 네트워크는 정보기술 응용 및 서비스 제공업체로, 고객의 컴퓨터 정보 시스템과 기업용 통신 시스템 구축을 위한 솔루션과 전문 서비스를 제공합니다. 사업 분야: 프로젝트 컨설팅, 솔루션 설계, 응용 시스템 개발, 기업 정보 시스템 등 서비스.

란더 네트워크는 통신 사업자 사이에서 좋은 브랜드 이미지를 갖추고 있으며, 당사의 통신 소프트웨어 제품군은 차이나텔레콤, 차이나모바일, 차이나유니콤, 차이나넷컴 등 여러 통신 사업자의 통신 시스템에 널리 쓰이고 있습니다. 란더 네트워크는 중국 무선 정보 서비스 산업의 우수한 상업 기관이자 시스템 공급업체의 대표가 되었으며, 정보 산업, 인터넷 및 데이터 통신 서비스 산업에서도 뛰어난 시스템·서비스 공급업체의 대표가 될 것입니다.

미래를 향한 란더 네트워크의 비전은 “더 나은 협업에 헌신한다”입니다. 우리는 인류의 협업에 더 아름다운 모습이 있다고 믿으며, 이를 탐색하고 그려 나가겠습니다!



O2OA(아오투) 개발 플랫폼은 **저장 란더쭝헝 네트워크 기술 주식회사** 가 구축하고 유지 관리합니다. O2OA(아오투)의 이름과 로고는 **저장 란더쭝헝 네트워크 기술 주식회사** 의 등록 상표입니다.

우리는 ❤️ 오픈소스 소프트웨어를 사랑합니다! [다른 오픈소스 프로젝트](https://github.com/o2oa)와 [블로그](https://my.oschina.net/o2oa)도 둘러보세요.


