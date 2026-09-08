#!/usr/bin/env python3
"""메뉴/권한 카탈로그 전체 export — views/Pub 미참조 후보 판정용 (2026-09-08).

실행 위치: 사내 dev 서버(psycopg2 와 sqms_jdbc_id / sqms_jdbc_pw 가 있는 로그인 셸). 로컬 PC 에서는 DB 에 닿지 않는다.
    export sqms_db_host=<dev DB 호스트>      # 서버의 배치 코드(di/comn/conn.py)와 같은 값. 지정 안 하면 오류로 멈춘다.
    python3 menu-catalog-export.py           # → menu.csv, menu-catalog.json 생성 (같은 폴더)
출력: 두 테이블을 필터 없이 전체. CSV 는 킷 d 의 --menu-csv 입력, JSON 은 보관용.
psql CLI 는 쓰지 않는다(사내 규약: 드라이버 커넥션만).
"""
import csv, json, os, sys
try:
    import psycopg2
except ImportError:
    sys.exit("psycopg2 가 없습니다. 배치 파이썬 환경(로그인 셸)에서 실행하세요.")

HOST = os.environ.get("sqms_db_host") or sys.exit("환경변수 sqms_db_host 를 지정하세요 (conn.py 의 jdbcHost 값).")
USER = os.environ.get("sqms_jdbc_id") or sys.exit("환경변수 sqms_jdbc_id 가 없습니다 (로그인 셸에서 실행).")
PW = os.environ.get("sqms_jdbc_pw") or sys.exit("환경변수 sqms_jdbc_pw 가 없습니다 (로그인 셸에서 실행).")
PORT = os.environ.get("sqms_db_port", "5432"); DB = os.environ.get("sqms_db_name", "sqms")

QUERIES = {
    # 메뉴/버튼 권한 마스터 — PGM_SORC_NM 이 프로그램(컴포넌트) 경로. 비활성(MENU_ACTVA_YN='N')·버튼(AUT_DIV_VAL='A') 포함 전체.
    "SQMOWN.CM_AUT_BAS": "SELECT AUT_ID, AUT_DIV_VAL, AUT_NM, AUT_SBST, PGM_ID, PGM_NM, PGM_SORC_NM, UP_MENU_ID, MENU_LVL_SEQ, MENU_ACTVA_YN, MENU_OTPUT_ODRG FROM SQMOWN.CM_AUT_BAS",
    # 역할-메뉴 구성 — 별칭·숨김 경로가 여기만 있을 수 있어 같이 export.
    "SQMOWN.CM_ROLE_MENU_COMPOSI_TXN": "SELECT ROLE_ID, AUT_ID, ROLE_MAPPG_DIV_CD, EFCT_FNS_DT FROM SQMOWN.CM_ROLE_MENU_COMPOSI_TXN",
}

conn = psycopg2.connect(database=DB, user=USER, password=PW, host=HOST, port=PORT)
conn.set_session(readonly=True, autocommit=True)
rows_out, json_out = [], []
with conn.cursor() as cur:
    for table, sql in QUERIES.items():
        cur.execute(sql)
        cols = [d[0].upper() for d in cur.description]
        n = 0
        for rec in cur:
            item = {"SOURCE_TABLE": table}
            for c, v in zip(cols, rec):
                item[c] = "" if v is None else str(v)
            rows_out.append(item); json_out.append(item); n += 1
        print(f"{table}: {n} rows")
conn.close()

fieldnames = ["SOURCE_TABLE"] + sorted({k for r in rows_out for k in r.keys() if k != "SOURCE_TABLE"})
with open("menu.csv", "w", newline="", encoding="utf-8-sig") as f:
    w = csv.DictWriter(f, fieldnames=fieldnames); w.writeheader(); w.writerows(rows_out)
with open("menu-catalog.json", "w", encoding="utf-8") as f:
    json.dump(json_out, f, ensure_ascii=False, indent=1)
print(f"menu.csv / menu-catalog.json written: {len(rows_out)} rows, {len(fieldnames)} columns")
