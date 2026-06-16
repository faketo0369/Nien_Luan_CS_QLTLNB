#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
==================================================================
TEST CRAWL: vanban.chinhphu.vn (Cổng TTĐT Chính phủ)
Thử 2-3 văn bản trước khi chạy hàng loạt
==================================================================
Nguồn: vanban.chinhphu.vn — HTML tĩnh, có đầy đủ metadata + link PDF
"""

import os
import re
import time
import unicodedata
import requests
from bs4 import BeautifulSoup

BASE_DIR = os.path.dirname(os.path.abspath(__file__))
DOCUMENTS_DIR = os.path.join(BASE_DIR, "documents")

HEADERS = {
    "User-Agent": (
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
        "AppleWebKit/537.36 (KHTML, like Gecko) "
        "Chrome/125.0.0.0 Safari/537.36"
    ),
    "Accept": "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8",
    "Accept-Language": "vi-VN,vi;q=0.9,en-US;q=0.8,en;q=0.7",
}

# ============================================================
# TEST: 3 văn bản có docid đã biết trên vanban.chinhphu.vn
# ============================================================
TEST_DOCS = [
    {
        "docid": "183188",
        "ten_expected": "Bộ Luật Dân sự 2015",
        "soHieu_expected": "91/2015/QH13",
    },
    {
        "docid": "176351",
        "ten_expected": "Luật Hôn nhân và Gia đình 2014",
        "soHieu_expected": "52/2014/QH13",
    },
    {
        "docid": "182750",
        "ten_expected": "Bộ luật Lao động 2019",
        "soHieu_expected": "45/2019/QH14",
    },
]

def remove_diacritics(text):
    nfkd = unicodedata.normalize("NFKD", text)
    return "".join(c for c in nfkd if not unicodedata.category(c).startswith("M"))

def sanitize_filename(text):
    text = remove_diacritics(text)
    text = text.lower().strip()
    text = re.sub(r"[^\w\s-]", "", text)
    text = re.sub(r"[\s_]+", "-", text)
    text = re.sub(r"-+", "-", text).strip("-")
    return text[:80]


def fetch_page(url, timeout=30):
    """Fetch với retry logic đơn giản."""
    try:
        resp = requests.get(url, headers=HEADERS, timeout=timeout)
        if resp.status_code == 403:
            print(f"  ⛔ 403 Forbidden — dừng, không retry. URL: {url}")
            return None
        if resp.status_code == 429:
            print(f"  ⛔ 429 Too Many Requests — dừng, không retry. URL: {url}")
            return None
        resp.raise_for_status()
        return resp
    except requests.exceptions.RequestException as e:
        print(f"  ❌ Lỗi: {e}")
        return None


def parse_metadata(soup):
    """Trích xuất metadata từ trang vanban.chinhphu.vn."""
    meta = {}

    # Tiêu đề
    title_el = soup.find("span", id=re.compile(r"lb_noidung"))
    if title_el:
        meta["ten"] = title_el.get_text(strip=True)

    # Bảng metadata
    content_div = soup.find("div", id=re.compile(r"_Content$"))
    if content_div:
        rows = content_div.find_all("tr")
        for row in rows:
            cols = row.find_all("td")
            if len(cols) >= 2:
                label = cols[0].get_text(strip=True)
                value = cols[1].get_text(strip=True)
                if "Số ký hiệu" in label:
                    meta["soHieu"] = value
                elif "Ngày ban hành" in label:
                    meta["ngayBanHanh"] = value
                elif "Loại văn bản" in label:
                    meta["loaiVanBan"] = value
                elif "Cơ quan ban hành" in label:
                    meta["coQuanBanHanh"] = value
                elif "Người ký" in label:
                    meta["nguoiKy"] = value
                elif "Trích yếu" in label:
                    meta["trichYeu"] = value

    # Link PDF
    pdf_links = []
    for a in soup.find_all("a", class_="view-file"):
        href = a.get("href", "")
        if href and ".pdf" in href.lower():
            pdf_links.append(href)
    # Cũng tìm link PDF trong href có datafiles.chinhphu.vn
    for a in soup.find_all("a", href=True):
        href = a["href"]
        if "datafiles.chinhphu.vn" in href and ".pdf" in href.lower():
            if href not in pdf_links:
                pdf_links.append(href)
    meta["pdf_links"] = pdf_links

    return meta


def download_pdf(url, save_path):
    """Tải file PDF về."""
    try:
        resp = requests.get(url, headers=HEADERS, timeout=60, stream=True)
        resp.raise_for_status()
        os.makedirs(os.path.dirname(save_path), exist_ok=True)
        with open(save_path, "wb") as f:
            for chunk in resp.iter_content(chunk_size=8192):
                f.write(chunk)
        size = os.path.getsize(save_path)
        return size
    except Exception as e:
        print(f"  ❌ Lỗi tải PDF: {e}")
        return 0


def save_html(content_html, metadata, save_path):
    """Lưu nội dung HTML gốc với wrapper UTF-8."""
    os.makedirs(os.path.dirname(save_path), exist_ok=True)
    ten = metadata.get("ten", "Văn bản pháp luật")
    so_hieu = metadata.get("soHieu", "")
    html = f"""<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>{ten} - {so_hieu}</title>
    <style>
        body {{ font-family: 'Times New Roman', serif; max-width: 900px; margin: 0 auto; padding: 20px; line-height: 1.6; }}
        h4.title {{ text-align: center; color: #003366; }}
        table {{ border-collapse: collapse; width: 100%; margin-bottom: 20px; }}
        td {{ padding: 6px 10px; border-bottom: 1px solid #ddd; }}
        td.col1 {{ font-weight: bold; width: 140px; background: #f5f5f5; }}
        .Content {{ margin-top: 20px; }}
        .source {{ text-align: center; font-size: 12px; color: #888; margin-top: 30px; }}
    </style>
</head>
<body>
{content_html}
<div class="source">
    <p>Nguồn: Cổng Thông tin điện tử Chính phủ (vanban.chinhphu.vn)</p>
</div>
</body>
</html>"""
    with open(save_path, "w", encoding="utf-8") as f:
        f.write(html)
    return len(html)


def test_crawl_one(docid, ten_expected, so_hieu_expected):
    """Test crawl một văn bản."""
    url = f"https://vanban.chinhphu.vn/default.aspx?pageid=27160&docid={docid}"
    print(f"\n{'='*60}")
    print(f"📄 Test: {ten_expected} ({so_hieu_expected})")
    print(f"   URL: {url}")
    print(f"   docid: {docid}")

    resp = fetch_page(url)
    if not resp:
        print("  ❌ Không truy cập được trang")
        return

    soup = BeautifulSoup(resp.content, "lxml")
    meta = parse_metadata(soup)

    print(f"\n  📋 METADATA TRÍCH XUẤT:")
    print(f"     Tên:           {meta.get('ten', '❌ KHÔNG TÌM THẤY')}")
    print(f"     Số hiệu:       {meta.get('soHieu', '❌ KHÔNG TÌM THẤY')}")
    print(f"     Ngày ban hành: {meta.get('ngayBanHanh', '❌ KHÔNG TÌM THẤY')}")
    print(f"     Loại VB:       {meta.get('loaiVanBan', '❌ KHÔNG TÌM THẤY')}")
    print(f"     CQ ban hành:   {meta.get('coQuanBanHanh', '❌ KHÔNG TÌM THẤY')}")
    print(f"     Người ký:      {meta.get('nguoiKy', '❌ KHÔNG TÌM THẤY')}")
    print(f"     Trích yếu:     {meta.get('trichYeu', '❌ KHÔNG TÌM THẤY')}")

    # PDF
    pdf_links = meta.get("pdf_links", [])
    if pdf_links:
        print(f"\n  📥 LINK PDF ({len(pdf_links)} file):")
        for link in pdf_links:
            print(f"     {link}")

        # Thử tải PDF đầu tiên
        pdf_url = pdf_links[0]
        test_dir = os.path.join(DOCUMENTS_DIR, "_test")
        filename = sanitize_filename(meta.get("trichYeu", ten_expected))
        pdf_path = os.path.join(test_dir, f"{filename}.pdf")
        print(f"\n  ⬇️  Đang tải PDF: {os.path.basename(pdf_url)}...")
        time.sleep(2)  # Rate limiting
        size = download_pdf(pdf_url, pdf_path)
        if size > 0:
            print(f"  ✅ PDF tải thành công: {size:,} bytes ({size/1024:.1f} KB)")
            print(f"     Lưu tại: {pdf_path}")
        else:
            print(f"  ❌ Tải PDF thất bại")
    else:
        print(f"\n  ⚠️  Không tìm thấy link PDF trên trang")

    # Lưu HTML metadata
    detail_div = soup.find("div", class_="Detail")
    if detail_div:
        html_content = str(detail_div)
        html_path = os.path.join(DOCUMENTS_DIR, "_test",
                                 f"{sanitize_filename(meta.get('trichYeu', ten_expected))}.html")
        html_size = save_html(html_content, meta, html_path)
        print(f"\n  📄 HTML metadata đã lưu: {html_size:,} ký tự")
        print(f"     Lưu tại: {html_path}")
    else:
        print(f"\n  ⚠️  Không tìm thấy div.Detail")

    print(f"\n  📊 HTML response tổng: {len(resp.content):,} bytes")


def main():
    print("╔══════════════════════════════════════════════════════╗")
    print("║  TEST CRAWL: vanban.chinhphu.vn                     ║")
    print("║  Thử 3 văn bản trước khi chạy hàng loạt            ║")
    print("╚══════════════════════════════════════════════════════╝")

    for doc in TEST_DOCS:
        test_crawl_one(doc["docid"], doc["ten_expected"], doc["soHieu_expected"])
        time.sleep(3)  # Delay giữa các request

    print(f"\n{'='*60}")
    print("✅ TEST HOÀN TẤT!")
    print(f"📂 Kiểm tra thư mục: {os.path.join(DOCUMENTS_DIR, '_test')}")
    print("👉 Mở file .html bằng browser và .pdf bằng reader để xác nhận nội dung")
    print("👉 Sau khi xác nhận OK, tôi sẽ viết script crawl đầy đủ 20 văn bản")


if __name__ == "__main__":
    main()
