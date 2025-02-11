# Might be a temporary solution, if we have time it should be rewritten in Java

import sys
import json
import yt_dlp

def get_video_info(url):
    try:
        # yt-dlp instance creation
        ydl_opts = {
            'quiet': True,
            'extract_flat': True  # Extract only metadatas
        }

        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            info_dict = ydl.extract_info(url, download=False)

            video_data = {
                "title": info_dict.get('title'),
                "description": info_dict.get('description'),
                "publish_date": info_dict.get('upload_date'),
                "channel_title": info_dict.get('uploader'),
                "keywords": info_dict.get('tags', []),
                "thumbnail": info_dict.get('thumbnail', None)
            }

            print(json.dumps(video_data, indent=4, ensure_ascii=False))

    except Exception as e:
        print("Error:", e)

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python3 get_yt_infos.py <YouTube_URL>")
    else:
        get_video_info(sys.argv[1])
