# Might be a temporary solution, if we have time it should be rewritten in Java

import sys
import re
from youtube_transcript_api import YouTubeTranscriptApi

def extract_video_id(url):
    pattern = r"(?:v=|\/)([0-9A-Za-z_-]{11})"
    match = re.search(pattern, url)
    if match:
        return match.group(1)
    else:
        raise ValueError("Invalid YouTube URL")

def get_transcript(url, languages=('en', 'fr')):
    try:
        video_id = extract_video_id(url)
        transcript = YouTubeTranscriptApi.get_transcript(video_id, languages=languages)
        text = " ".join([entry['text'] for entry in transcript])
        print(text)
    except:
        print("Subtitles are disabled for this video, you have to deal with it")

if __name__ == "__main__":
    if len(sys.argv) < 2:
        print("Usage: python3 get_transcript.py <YouTube_URL>")
    else:
        get_transcript(sys.argv[1])