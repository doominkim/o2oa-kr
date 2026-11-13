#!/usr/bin/env python3
"""O2OA 기본 콘텐츠 가져오기.

사용법:
  O2_ADMIN_PASSWORD=... python3 deploy/content/import.py --url http://서버주소 [--user xadmin] [approval] [boards]

- approval: deploy/content/approval/*.xapp(프로세스 플랫폼 앱)을 가져온다. 같은 id의 앱이 있으면 덮어쓰고(cover), 없으면 새로 만든다(create).
  전자결재의 휴가 신청서가 쓰는 근태(attendancev2) 휴가 유형 5개(연차·오전반차·오후반차·병가·경조사)가 없으면 만든다.
- 앱 종류마다 import_<종류>() 함수를 하나씩 두고, 새 종류는 IMPORTERS에 추가한다.
비밀번호는 출력하지 않는다."""
import argparse
import getpass
import json
import os
import sys
import urllib.error
import urllib.parse
import urllib.request
import uuid
from pathlib import Path

HERE = Path(__file__).resolve().parent
LEAVE_TYPES = ["연차", "오전반차", "오후반차", "병가", "경조사"]


class Api:
    def __init__(self, base, user, password):
        self.base = base.rstrip("/")
        self.token = None
        self.token = self.call("POST", "/x_organization_assemble_authentication/jaxrs/authentication",
                               {"credential": user, "password": password})["token"]

    def call(self, method, path, body=None):
        data = None if body is None else json.dumps(body, ensure_ascii=False).encode()
        headers = {"Content-Type": "application/json; charset=utf-8"}
        if self.token:
            headers["x-token"] = self.token
        req = urllib.request.Request(self.base + urllib.parse.quote(path, safe="/:?=&%"), data=data,
                                     method=method, headers=headers)
        try:
            with urllib.request.urlopen(req, timeout=300) as r:
                out = json.loads(r.read().decode())
        except urllib.error.HTTPError as e:
            out = json.loads(e.read().decode() or "{}")
        if out.get("type") != "success":
            raise RuntimeError(f"{method} {path}: {out.get('message')}")
        return out.get("data")


# --- 근태 ---------------------------------------------------------------------------------------
def ensure_leave_types(api):
    base = "/x_attendance_assemble_control/jaxrs/v2/leavemanager"
    have = {t["name"] for t in api.call("GET", base + "/type/list/all") if t.get("active") is not False}
    for name in LEAVE_TYPES:
        if name not in have:
            api.call("POST", base + "/type", {"name": name, "quotaType": "UNLIMITED", "unit": "DAY", "isPaid": True, "active": True})
            print(f"  근태 휴가 유형 생성: {name}")


# --- 프로세스 플랫폼(결재) 앱 -----------------------------------------------------------------------
def import_process_app(api, path):
    app = json.loads(Path(path).read_text(encoding="utf-8"))
    app.pop("flag", None)
    designer = "/x_processplatform_assemble_designer/jaxrs"
    try:
        api.call("GET", f"{designer}/application/{app['id']}")
        exists = True
    except RuntimeError:
        exists = False
    api.call("PUT", f"{designer}/input/{'cover' if exists else 'create'}", app)
    # 덮어쓰기는 활동(Manual·End 등) 캐시를 비우지 않으므로, 디자이너의 '사용' 동작으로 캐시를 갱신한다.
    for p in app.get("processList", []):
        api.call("GET", f"{designer}/process/{p['id']}/enabled")
    print(f"  {app['name']}: {'덮어씀' if exists else '새로 만듦'} "
          f"(양식 {len(app.get('formList', []))}, 프로세스 {len(app.get('processList', []))})")


def import_approval(api):
    ensure_leave_types(api)
    for f in sorted((HERE / "approval").glob("*.xapp")):
        import_process_app(api, f)


# --- 게시판(CMS) — 앱센터 내보내기(.xapp) 형식 ------------------------------------------------------
def upload(api, path, file):
    boundary = uuid.uuid4().hex
    body = (f"--{boundary}\r\nContent-Disposition: form-data; name=\"file\"; filename=\"{file.name}\"\r\n"
            f"Content-Type: application/octet-stream\r\n\r\n").encode() + file.read_bytes() + f"\r\n--{boundary}--\r\n".encode()
    req = urllib.request.Request(api.base + path, data=body, method="PUT",
                                 headers={"Content-Type": f"multipart/form-data; boundary={boundary}", "x-token": api.token})
    with urllib.request.urlopen(req, timeout=300) as r:
        out = json.loads(r.read().decode())
    if out.get("type") != "success":
        raise RuntimeError(f"PUT {path}: {out.get('message')}")
    return out.get("data")


def import_boards(api):
    for f in sorted((HERE / "boards").glob("*.xapp")):
        cmp = upload(api, "/x_program_center/jaxrs/module/compare/upload", f)
        apps = cmp.get("cmsList", [])
        api.call("PUT", f"/x_program_center/jaxrs/module/write/{cmp['flag']}", {
            "cmsList": [{"id": a["id"], "method": "cover" if a.get("exist") else "create"} for a in apps],
            "processPlatformList": [], "portalList": [], "queryList": [], "serviceModuleList": []})
        print(f"  {f.name}: " + ", ".join(f"{a['name']}({'덮어씀' if a.get('exist') else '새로 만듦'})" for a in apps))


IMPORTERS = {"approval": import_approval, "boards": import_boards}


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--url", required=True, help="예: http://121.167.129.36")
    ap.add_argument("--user", default="xadmin")
    ap.add_argument("kinds", nargs="*", default=list(IMPORTERS), help=f"가져올 종류 (기본: 전부) {list(IMPORTERS)}")
    args = ap.parse_args()
    password = os.environ.get("O2_ADMIN_PASSWORD") or getpass.getpass(f"{args.user} 비밀번호: ")
    api = Api(args.url, args.user, password)
    for kind in args.kinds:
        if kind not in IMPORTERS:
            sys.exit(f"알 수 없는 종류: {kind}")
        print(f"[{kind}]")
        IMPORTERS[kind](api)


if __name__ == "__main__":
    main()
