from flask import Flask, render_template, jsonify, Response, request
from flask_restful import Api, Resource
from functools import wraps
import csv
from werkzeug.security import generate_password_hash, check_password_hash

movies = []

app = Flask(__name__)
app.secret_key = "Thanks for the semester!"
api = Api(app, prefix="/api")

auth_user = [{
    "name": "admin",
    "password": generate_password_hash("plaintextboo")
}]

## Auth Decorator and Helpers
# @app.route("/secret_page")
# @requires_auth
# def secret_page():
#     return render_template("secret-page.html")

def requires_auth(f):
    @wraps(f)
    def wrapped_view(*args, **kwargs):
        auth = request.authorization
        if not auth:
            return authenticate()
        if not check_auth(auth.username, auth.password):
            return authenticate()
        return f(*args, **kwargs)
    return wrapped_view

def check_auth(username, password):
    return username == "admin" and check_password_hash(auth_user[0]["password"], password)

def authenticate():
    return Response( "Could not verify your access level for that URL.\n You have to login with proper credentials", 401, {'WWW-Authenticate': 'Basic realm="Login Required"'})


## Flask Routes


@app.route("/")
@requires_auth
def landing():
    return render_template("base.html")


## Flask-RESTful Resources and Routes

# Create Movie Collection Resource
class MovieListResource(Resource):
    def get(self):
        return jsonify(movies)


# Create Movie Item Resource
class MovieResource(Resource):
    def get(self, movie_id=None):
        movie = [m for m in movies if m["id"] == movie_id][0]
        return jsonify(movie)


api.add_resource(MovieListResource, "/movies/", endpoint="movies")
api.add_resource(MovieResource, "/movies/<int:movie_id>", endpoint="movie")

## Populate Movie List from CSV encoding='UTF-8'
with open("./data/bo.csv", "r") as bo:
    read_bo = csv.DictReader(bo, delimiter=",")
    for m in read_bo:
        try:
            ot = int(m["Opening Theaters"].replace(",", ""))
        except:
            ot = None
        try:
            tt = int(m["Theaters"].replace(",", ""))
        except:
            tt = None
        try:
            tg = float(m["Total Gross"].strip().replace("$", "").replace(",", ""))
        except:
            tg = None
        try:
            og = float(m["Opening"].strip().replace("$", "").replace(",", ""))
        except:
            og = None
        movies.append(
            {
                "id": int(m["ID"]),
                "title": m["Movie Title"],
                "studio": m["Studio"],
                "total_gross": tg,
                "total_theaters": tt,
                "opening_gross": og,
                "opening_theaters": ot,
            }
        )

if __name__ == "__main__":
    app.run()
