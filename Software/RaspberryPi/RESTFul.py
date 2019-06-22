#!flask/bin/python
from flask import Flask, jsonify, request, json
import sqlite3
import time

app = Flask(__name__)

#@app.route('/test', methods=['POST'])
#def get_tasks():
#    date1 = request.json['dateBegin']
#    return jsonify(date1)

#@app.route('/db1', methods=['GET'])
#def test2():
#    c.execute('INSERT INTO Spannungen(currentOut, currentIn, currentBat, date) VALUES(1,1,1,datetime("now"))')
#    return 'Datensatz Hinzugefügt'

@app.route('/smartminicamper/db', methods=['GET'])
def read_from_db():
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    c.execute('select * FROM Spannungen')
    data = c.fetchall()
    payload = []
    content = {}
    for result in data:
        content = {'id': result[0], 'date': result[1], 'powerUsage': result[2], 'powerCharge': result[3], 'batteryVoltage': result[4]}
        payload.append(content)
        content = {}
    conn.commit()
    conn.close()
    return jsonify(payload)

@app.route('/smartminicamper/db_aktuelle_daten', methods=['GET'])
def read_from_dbaktuell():
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    c.execute('select * FROM Spannungen order by id DESC LIMIT 1')
    data = c.fetchall()
    payload = []
    content = {}
    for result in data:
        content = {'id': result[0], 'date': result[1], 'powerUsage': result[2], 'powerCharge': result[3], 'batteryVoltage': result[4]}
        payload.append(content)
        content = {}
    conn.commit()
    conn.close()
    return jsonify(payload)

@app.route('/smartminicamper/db_zeitraum', methods=['POST'])
def read_from_dbzeitraum():
    conn = sqlite3.connect('test.db')
    c = conn.cursor()
    dateB = request.json['dateBegin']
    dateE = request.json['dateEnd']
    c.execute("select * FROM Spannungen WHERE date BETWEEN '" + dateB + "' and '" + dateE + "'")
    data = c.fetchall()
    payload = []
    content = {}
    for result in data:
        content = {'id': result[0], 'date': result[1], 'powerUsage': result[2], 'powerCharge': result[3], 'batteryVoltage': result[4]}
        payload.append(content)
        content = {}
    conn.commit()
    conn.close()
    return jsonify(payload)

from flask import abort

from flask import make_response

@app.errorhandler(404)
def not_found(error):
    return make_response(jsonify({'error': 'Not found'}), 404)

if __name__ == '__main__':
    app.run(host='0.0.0.0')

    
