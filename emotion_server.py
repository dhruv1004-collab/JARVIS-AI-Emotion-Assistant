from flask import Flask, request, jsonify
import base64
import numpy as np
import cv2

app = Flask(__name__)

# Load cascades for facial features
face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
smile_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_smile.xml')
eye_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_eye.xml')

def detect_emotion(frame):
    gray = cv2.cvtColor(frame, cv2.COLOR_BGR2GRAY)
    cv2.equalizeHist(gray, gray)

    faces = face_cascade.detectMultiScale(gray, 1.1, 5)

    if len(faces) == 0:
        return 'neutral'

    # Analyze first face
    (x, y, w, h) = faces[0]
    face_roi = gray[y:y+h, x:x+w]

    # Detect smile inside face
    smiles = smile_cascade.detectMultiScale(
        face_roi, 1.7, 20
    )

    # Detect eyes inside face
    eyes = eye_cascade.detectMultiScale(face_roi, 1.1, 5)

    # Simple emotion logic
    if len(smiles) > 0:
        return 'happy'
    elif len(eyes) == 0:
        return 'sad'
    elif len(eyes) >= 2:
        return 'neutral'
    else:
        return 'surprised'

@app.route('/detect-emotion', methods=['POST'])
def emotion_endpoint():
    try:
        data = request.get_json()
        image_base64 = data['image']

        # Decode Base64 image
        image_bytes = base64.b64decode(image_base64)
        np_array = np.frombuffer(image_bytes, np.uint8)
        frame = cv2.imdecode(np_array, cv2.IMREAD_COLOR)

        emotion = detect_emotion(frame)

        return jsonify({
            'emotion': emotion
        })

    except Exception as e:
        return jsonify({'error': str(e)}), 500

if __name__ == '__main__':
    print("JARVIS Emotion Server starting on port 5000...")
    app.run(port=5000)