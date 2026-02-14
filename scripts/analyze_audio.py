#!/usr/bin/env python3
"""
音频分析脚本 - 提取演唱音频的声学特征
用于歌曲评测系统，由 Java 后端通过 ProcessBuilder 调用

输入：音频文件路径，可选的参考音频路径
输出：JSON 格式的分析结果
"""

import sys
import json
import os
import tempfile
import subprocess
import warnings

# 禁用所有警告信息，确保输出只有纯净的 JSON
warnings.filterwarnings("ignore")

import numpy as np
import librosa
import parselmouth
from parselmouth.praat import call


def convert_to_wav(input_path):
    """将音频文件转换为 WAV 格式（用于 Parselmouth 分析）"""
    if input_path.lower().endswith('.wav'):
        return input_path, False
    
    temp_wav = tempfile.NamedTemporaryFile(suffix='.wav', delete=False)
    temp_wav.close()
    
    try:
        result = subprocess.run(
            ['ffmpeg', '-y', '-i', input_path, '-ar', '22050', '-ac', '1', temp_wav.name],
            capture_output=True,
            text=True,
            timeout=60
        )
        if result.returncode == 0:
            return temp_wav.name, True
        else:
            os.unlink(temp_wav.name)
            return None, False
    except FileNotFoundError:
        os.unlink(temp_wav.name)
        return None, False
    except Exception:
        if os.path.exists(temp_wav.name):
            os.unlink(temp_wav.name)
        return None, False


def extract_pitch_features(audio_path):
    """提取音高特征"""
    y, sr = librosa.load(audio_path, sr=22050)
    
    pitches, magnitudes = librosa.piptrack(y=y, sr=sr, fmin=50, fmax=2000)
    
    pitch_values = []
    for t in range(pitches.shape[1]):
        index = magnitudes[:, t].argmax()
        pitch = pitches[index, t]
        if pitch > 0:
            pitch_values.append(float(pitch))
    
    if not pitch_values:
        return {
            "mean_pitch": 0,
            "pitch_range": 0,
            "pitch_stability": 0,
            "pitch_values": []
        }
    
    pitch_array = np.array(pitch_values)
    
    return {
        "mean_pitch": float(np.mean(pitch_array)),
        "pitch_range": float(np.max(pitch_array) - np.min(pitch_array)),
        "pitch_stability": float(100 - np.std(pitch_array) / np.mean(pitch_array) * 100) if np.mean(pitch_array) > 0 else 0,
        "pitch_values": pitch_values[:100]
    }


def extract_rhythm_features(audio_path):
    """提取节奏特征"""
    y, sr = librosa.load(audio_path, sr=22050)
    
    tempo, beat_frames = librosa.beat.beat_track(y=y, sr=sr)
    beat_times = librosa.frames_to_time(beat_frames, sr=sr)
    
    if len(beat_times) < 2:
        return {
            "tempo": float(tempo) if isinstance(tempo, (int, float)) else float(tempo[0]) if len(tempo) > 0 else 0,
            "beat_regularity": 0,
            "rhythm_score": 50
        }
    
    beat_intervals = np.diff(beat_times)
    beat_regularity = float(100 - np.std(beat_intervals) / np.mean(beat_intervals) * 100) if np.mean(beat_intervals) > 0 else 0
    
    tempo_value = float(tempo) if isinstance(tempo, (int, float)) else float(tempo[0]) if len(tempo) > 0 else 0
    
    rhythm_score = min(100, max(0, beat_regularity))
    
    return {
        "tempo": tempo_value,
        "beat_regularity": beat_regularity,
        "rhythm_score": rhythm_score
    }


def extract_voice_features(audio_path):
    """使用 Parselmouth 提取嗓音特征"""
    wav_path, is_temp = convert_to_wav(audio_path)
    if wav_path is None:
        return {
            "jitter": 0.02,
            "shimmer": 0.05,
            "hnr": 15.0,
            "jitter_score": 60,
            "shimmer_score": 75,
            "hnr_score": 60,
            "voice_score": 65,
            "voice_quality": "良好",
            "note": "无法转换音频格式（需要安装 ffmpeg），使用默认值"
        }
    
    try:
        sound = parselmouth.Sound(wav_path)
        
        pitch = call(sound, "To Pitch", 0.0, 75, 600)
        
        point_process = call(sound, "To PointProcess (periodic, cc)", 75, 600)
        
        try:
            jitter = call(point_process, "Get jitter (local)", 0, 0, 0.0001, 0.02, 1.3)
        except:
            jitter = 0.02
        
        try:
            shimmer = call([sound, point_process], "Get shimmer (local)", 0, 0, 0.0001, 0.02, 1.3, 1.6)
        except:
            shimmer = 0.05
        
        try:
            harmonicity = call(sound, "To Harmonicity (cc)", 0.01, 75, 0.1, 1.0)
            hnr = call(harmonicity, "Get mean", 0, 0)
        except:
            hnr = 15.0
        
        jitter_score = max(0, min(100, 100 - jitter * 2000))
        shimmer_score = max(0, min(100, 100 - shimmer * 500))
        hnr_score = max(0, min(100, hnr * 4))
        
        voice_score = (jitter_score * 0.3 + shimmer_score * 0.3 + hnr_score * 0.4)
        
        voice_quality = "优秀"
        if voice_score < 60:
            voice_quality = "需要改进"
        elif voice_score < 75:
            voice_quality = "良好"
        elif voice_score < 90:
            voice_quality = "很好"
        
        return {
            "jitter": float(jitter),
            "shimmer": float(shimmer),
            "hnr": float(hnr) if not np.isnan(hnr) else 15.0,
            "jitter_score": jitter_score,
            "shimmer_score": shimmer_score,
            "hnr_score": hnr_score,
            "voice_score": voice_score,
            "voice_quality": voice_quality
        }
    finally:
        if is_temp and wav_path and os.path.exists(wav_path):
            os.unlink(wav_path)


def extract_timbre_features(audio_path):
    """提取音色特征"""
    y, sr = librosa.load(audio_path, sr=22050)
    
    mfccs = librosa.feature.mfcc(y=y, sr=sr, n_mfcc=13)
    mfcc_mean = np.mean(mfccs, axis=1).tolist()
    
    spectral_centroid = librosa.feature.spectral_centroid(y=y, sr=sr)
    brightness = float(np.mean(spectral_centroid))
    
    spectral_rolloff = librosa.feature.spectral_rolloff(y=y, sr=sr)
    warmth = float(np.mean(spectral_rolloff))
    
    brightness_level = "明亮" if brightness > 2000 else "柔和" if brightness > 1500 else "低沉"
    
    return {
        "mfcc": mfcc_mean,
        "brightness": brightness,
        "warmth": warmth,
        "brightness_level": brightness_level
    }


def extract_energy_features(audio_path):
    """提取能量/气息特征"""
    y, sr = librosa.load(audio_path, sr=22050)
    
    rms = librosa.feature.rms(y=y)
    rms_mean = float(np.mean(rms))
    rms_std = float(np.std(rms))
    
    energy_stability = max(0, min(100, 100 - rms_std / rms_mean * 100)) if rms_mean > 0 else 0
    
    dynamic_range = float(np.max(rms) - np.min(rms))
    
    return {
        "energy_mean": rms_mean,
        "energy_stability": energy_stability,
        "dynamic_range": dynamic_range,
        "breath_control_score": energy_stability
    }


def compare_with_reference(user_audio_path, reference_audio_path):
    """与参考音频对比（DTW 对齐）"""
    y_user, sr = librosa.load(user_audio_path, sr=22050)
    y_ref, sr = librosa.load(reference_audio_path, sr=22050)
    
    mfcc_user = librosa.feature.mfcc(y=y_user, sr=sr, n_mfcc=13)
    mfcc_ref = librosa.feature.mfcc(y=y_ref, sr=sr, n_mfcc=13)
    
    D, wp = librosa.sequence.dtw(mfcc_user, mfcc_ref, subseq=True)
    
    dtw_distance = D[-1, -1] / len(wp)
    
    similarity = max(0, min(100, 100 - dtw_distance * 0.5))
    
    return {
        "dtw_distance": float(dtw_distance),
        "similarity_score": float(similarity),
        "has_reference": True
    }


def analyze_audio(audio_path, reference_path=None):
    """主分析函数"""
    result = {
        "success": True,
        "audio_path": audio_path,
        "features": {}
    }
    
    try:
        result["features"]["pitch"] = extract_pitch_features(audio_path)
    except Exception as e:
        result["features"]["pitch"] = {"error": str(e)}
    
    try:
        result["features"]["rhythm"] = extract_rhythm_features(audio_path)
    except Exception as e:
        result["features"]["rhythm"] = {"error": str(e)}
    
    try:
        result["features"]["voice"] = extract_voice_features(audio_path)
    except Exception as e:
        result["features"]["voice"] = {"error": str(e)}
    
    try:
        result["features"]["timbre"] = extract_timbre_features(audio_path)
    except Exception as e:
        result["features"]["timbre"] = {"error": str(e)}
    
    try:
        result["features"]["energy"] = extract_energy_features(audio_path)
    except Exception as e:
        result["features"]["energy"] = {"error": str(e)}
    
    if reference_path:
        try:
            result["features"]["comparison"] = compare_with_reference(audio_path, reference_path)
        except Exception as e:
            result["features"]["comparison"] = {"error": str(e), "has_reference": False}
    else:
        result["features"]["comparison"] = {"has_reference": False}
    
    scores = calculate_overall_scores(result["features"])
    result["scores"] = scores
    
    return result


def calculate_overall_scores(features):
    """计算综合评分"""
    pitch_score = features.get("pitch", {}).get("pitch_stability", 70)
    rhythm_score = features.get("rhythm", {}).get("rhythm_score", 70)
    voice_score = features.get("voice", {}).get("voice_score", 70)
    breath_score = features.get("energy", {}).get("breath_control_score", 70)
    
    pitch_score = min(100, max(0, pitch_score))
    rhythm_score = min(100, max(0, rhythm_score))
    voice_score = min(100, max(0, voice_score))
    breath_score = min(100, max(0, breath_score))
    
    overall = pitch_score * 0.3 + rhythm_score * 0.25 + voice_score * 0.25 + breath_score * 0.2
    
    return {
        "overall": round(overall, 1),
        "pitch": round(pitch_score, 1),
        "rhythm": round(rhythm_score, 1),
        "voice": round(voice_score, 1),
        "breath": round(breath_score, 1)
    }


if __name__ == "__main__":
    if len(sys.argv) < 2:
        print(json.dumps({"success": False, "error": "请提供音频文件路径"}))
        sys.exit(1)
    
    audio_path = sys.argv[1]
    reference_path = sys.argv[2] if len(sys.argv) > 2 else None
    
    try:
        result = analyze_audio(audio_path, reference_path)
        print(json.dumps(result, ensure_ascii=False))
    except Exception as e:
        print(json.dumps({"success": False, "error": str(e)}))
        sys.exit(1)
