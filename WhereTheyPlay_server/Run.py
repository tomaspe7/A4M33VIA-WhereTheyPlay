from Server import flask

if __name__ == '__main__':
    flask.run(port=8088, debug=False, threaded=True, host="0.0.0.0")