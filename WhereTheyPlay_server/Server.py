from flask import Flask
from flask import request
from flask_cors import CORS

flask = Flask(__name__)
cors = CORS(flask)
stadium_names = {"Deportivo Alavés": "Estadio de Mendizorroza",
                 "Athletic Club": "Estadio San Mamés",
                 "Club Atlético de Madrid": "Vicente Calderón",
                 "FC Barcelona": "Camp Nou",
                 "Real Betis": "Estadio Benito Villamarín",
                 "RC Celta de Vigo": "Estadio de Balaídos",
                 "RC Deportivo La Coruna": "Ciudad Deportiva de Riazor",
                 "SD Eibar": "Ipurua",
                 "RCD Espanyol": "Cornellà-El Prat",
                 "Granada CF": "Estadio Nuevo Los Cármenes",
                 "UD Las Palmas": "Estadio Gran Canaria",
                 "CD Leganes": "Estadio Municipal Butarque",
                 "Málaga CF": "Estadio La Rosaleda",
                 "CA Osasuna": "Estadio El Sadar",
                 "Real Madrid CF": "Estadio Santiago Bernabéu",
                 "Real Sociedad de Fútbol": "Estadio Anoeta",
                 "Sevilla FC": "Estadio Ramón Sánchez-Pizjuán",
                 "Sporting Gijón": "Estadio El Molinón",
                 "Valencia CF": "Estadio de Mestalla",
                 "Villarreal CF": "Estadio El Madrigal"}


@flask.route('/stadium', methods=['GET'])
def get_hello_world():
    club = request.args['club']
    return stadium_names[club]
