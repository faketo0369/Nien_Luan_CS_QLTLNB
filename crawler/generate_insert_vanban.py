import csv
import re
import unicodedata
from datetime import datetime
from pathlib import Path


BASE_DIR = Path(__file__).resolve().parents[1]
DEFAULT_CSV = BASE_DIR / "crawler" / "vanban_phapluat.csv"
FALLBACK_CSV = BASE_DIR / "vanban_phapluat.csv"
OUTPUT_SQL = BASE_DIR / "database" / "03_insert_vanban.sql"


def sql_string(value):
    if value is None:
        return "NULL"
    text = str(value).strip()
    if not text:
        return "NULL"
    return "'" + text.replace("\\", "\\\\").replace("'", "''") + "'"


def sql_date(value):
    value = (value or "").strip()
    if not value:
        return "NULL"
    for fmt in ("%d/%m/%Y", "%Y-%m-%d"):
        try:
            return "'" + datetime.strptime(value, fmt).strftime("%Y-%m-%d") + "'"
        except ValueError:
            continue
    return "NULL"


def file_extension(path):
    suffix = Path(path or "").suffix.lower().lstrip(".")
    return suffix or None


def load_rows(csv_path):
    with csv_path.open("r", encoding="utf-8-sig", newline="") as f:
        reader = csv.DictReader(f)
        required = {"ten", "soHieu", "ngayBanHanh", "linhVuc", "loaiVanBan", "duongDanFile"}
        missing = required - set(reader.fieldnames or [])
        if missing:
            raise ValueError(f"CSV missing columns: {', '.join(sorted(missing))}")
        return list(reader)


def normalized_key(value):
    value = (value or "").replace("\u0111", "d").replace("\u0110", "D")
    value = unicodedata.normalize("NFKD", value)
    value = "".join(ch for ch in value if not unicodedata.combining(ch))
    value = re.sub(r"\s+", " ", value).strip().lower()
    return value


def unique_values(rows, column):
    seen = set()
    values = []
    for row in rows:
        value = (row.get(column) or "").strip()
        key = normalized_key(value)
        if value and key not in seen:
            seen.add(key)
            values.append(value)
    return values


def main():
    csv_path = DEFAULT_CSV if DEFAULT_CSV.exists() else FALLBACK_CSV
    if not csv_path.exists():
        raise FileNotFoundError(f"CSV not found: {DEFAULT_CSV} or {FALLBACK_CSV}")

    rows = load_rows(csv_path)

    lines = [
        "USE qltl_luat_dan_su;",
        "SET NAMES utf8mb4;",
        "",
        "-- Danh muc/linh vuc phap ly, tranh trung theo DM_ten.",
    ]

    for name in unique_values(rows, "linhVuc"):
        lines.append(
            "INSERT INTO DANH_MUC (DM_ten) VALUES "
            f"({sql_string(name)}) "
            "ON DUPLICATE KEY UPDATE DM_ten = VALUES(DM_ten);"
        )

    lines.extend(["", "-- Loai tai lieu phap ly, tranh trung theo LTLPL_ten."])
    for name in unique_values(rows, "loaiVanBan"):
        lines.append(
            "INSERT INTO LOAI_TAI_LIEU_PHAP_LY (LTLPL_ten) VALUES "
            f"({sql_string(name)}) "
            "ON DUPLICATE KEY UPDATE LTLPL_ten = VALUES(LTLPL_ten);"
        )

    lines.extend(["", "-- Tai lieu phap ly da crawl, tranh trung theo (TL_soHieu, TL_ten)."])
    for row in rows:
        ten = row.get("ten")
        so_hieu = row.get("soHieu")
        ngay_ban_hanh = row.get("ngayBanHanh")
        linh_vuc = row.get("linhVuc")
        loai = row.get("loaiVanBan")
        duong_dan = row.get("duongDanFile")
        ext = file_extension(duong_dan)
        lines.append(
            "INSERT INTO TAI_LIEU (DM_id, LTLPL_id, TL_ten, TL_duongDan, TL_dinhDang, "
            "TL_nguoiTao, TL_ngayTao, TL_ngayBanHanh, TL_daXoa, TL_baoMat, TL_soHieu) VALUES ("
            f"(SELECT DM_id FROM DANH_MUC WHERE DM_ten = {sql_string(linh_vuc)} LIMIT 1), "
            f"(SELECT LTLPL_id FROM LOAI_TAI_LIEU_PHAP_LY WHERE LTLPL_ten = {sql_string(loai)} LIMIT 1), "
            f"{sql_string(ten)}, {sql_string(duong_dan)}, {sql_string(ext)}, "
            f"'crawler', NOW(), {sql_date(ngay_ban_hanh)}, FALSE, 'NOI_BO', {sql_string(so_hieu)}"
            ") ON DUPLICATE KEY UPDATE "
            "DM_id = VALUES(DM_id), "
            "LTLPL_id = VALUES(LTLPL_id), "
            "TL_duongDan = VALUES(TL_duongDan), "
            "TL_dinhDang = VALUES(TL_dinhDang), "
            "TL_ngayBanHanh = VALUES(TL_ngayBanHanh), "
            "TL_daXoa = VALUES(TL_daXoa);"
        )

    OUTPUT_SQL.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"Created {OUTPUT_SQL} from {csv_path} ({len(rows)} rows).")


if __name__ == "__main__":
    main()
