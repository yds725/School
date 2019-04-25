from flask import Flask, request, session, render_template, abort, flash, redirect, url_for
from models import db, Player, Game
import datetime
import os
import json

app = Flask(__name__)
app.secret_key='Development Key'
app.debug = True
app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///" + os.path.join(
    app.root_path, "connect4.db"
)

items = []
# Suppress deprecation warning
app.config["SQLALCHEMY_TRACK_MODIFICATIONS"] = False

db.init_app(app)

@app.route("/")
@app.route("/landing/<username>", methods = ["GET", "POST"])
def home(username = None):
    if "logged_in" in session and session.get("logged_in") == username:
        user = Player.query.filter_by(username=username).first()
        logged = True

        game_scores = Game.query.order_by(Game.turn).filter((Game.player_one_id == user.id) | (Game.player_two_id == user.id) ).limit(10).all()
        all_scores = Game.query.order_by(Game.turn).limit(10).all()
        # list1 = ["" for i in range(10)]
        # list2 = ["" for i in range(10)]

        # for i in range(10):
        #     list1[i] = game_scores[i]
        if request.method == "GET":
        #if "logged_in" in session:
            games = Game.query.filter((Game.player_one_id == user.id) | (Game.player_two_id == user.id) ).all()
            return render_template("landing.html", games=games, username = user.username, loggedIn = logged, scores = game_scores, whole_scores = all_scores, userID = user.id)
        elif request.method == "POST":
            player2_username = request.form["game_button"]
            p1 = user
            p2 = Player.query.filter(Player.username == player2_username).first()

            gameQuery = Game.query.filter_by(player_one_id = p1.id, player_two_id = p2.id).first()
            gameID = gameQuery.id
            return redirect(url_for("game", game_id = gameID))
            # if("create_game_button" in request.form):
            #     g = Game()
            #     g.session.add(g)
            #     player1 = user
            #     player2 = Player.query.filter(Player.username == request.form["new_player2_username"]).first()
            #     g.player_one = player1
            #     g.player_two = player2
            #     db.session.commit()
            #go to that game
            # player2_username = request.form["play_game_button"]
            # p1 = user
            # p2 = Player.query.filter(Player.username == request.form["play_game_button"]).first()

            # gameQuery = Game.query.filter_by(player_one_id = p1.id, player_two_id = p2.id).first()
            # gameID = gameQuery.id
            # return redirect(url_for("game", game_id = gameID))
    else:
        logged = False
        all_scores = Game.query.order_by(Game.turn).all()
        #games = db.session.query(Game).all()
        return render_template("landing.html", loggedIn = logged, whole_scores = all_scores)

    #if request.method == "POST":

@app.route("/create_game/", methods = ["POST"])
def create_game():
    p1 = request.form["player1_name"]
    p2 = request.form["player2_name"]
    items.append([p1, p2])
    g = Game()
    db.session.add(g)
    player1 = Player.query.filter(Player.username == p1).first()
    player2 = Player.query.filter(Player.username == p2).first()
    g.player_one = player1
    g.player_two = player2
    g.creator_id = player1.id
    db.session.commit()
    return "OK!"

@app.route("/show_game/")
def show_game():
    return json.dumps(items)

@app.route('/delete/', methods=['POST'])
def delete():
    if "logged_in" in session:
        user = Player.query.filter_by(username = session.get("logged_in")).first()
        db.session.delete(Game.query.get(request.form['game_id']))
        db.session.commit()
        return redirect(url_for('home', username = user.username ))

@app.route("/login/", methods = ["GET", "POST"])
def login():
    if request.method == "GET":
        return render_template("login.html")

    if request.method == "POST":
        user = Player.query.filter((Player.username == request.form["username"]) & (Player.password == request.form["password"])).first()
        if user is None:
            flash("Invalid username or password. Check those!")
            return redirect(url_for("login"))
        
        session["logged_in"] = user.username
        return redirect(url_for("home", username = user.username) )

@app.route("/logout/")
def logout():
    if "logged_in" in session:
        user = Player.query.filter_by(username = session.get("logged_in")).first()
        session.pop("logged_in", None)
        flash("You have been logged out!")
        return redirect(url_for("home", username = None))
    
    return redirect(url_for("home"))

@app.route("/register/", methods = ["GET", "POST"])
def register():
    if request.method == "GET":
        return render_template("register.html")
    
    if request.method == "POST":
        if request.form["password"] != request.form["password2"]:
            flash("Passwords do not match. Try again!")
            return redirect(url_for("register"))
        elif Player.query.filter_by(username = request.form["username"]).scalar() is not None:
            flash("Duplicate Username. Try with other username!")
            return redirect(url_for("register"))
        
        birthdate = datetime.datetime.strptime('07/25/1992', '%m/%d/%Y').date()
        newUser = Player(username = request.form["username"], password = request.form["password"], birthday = birthdate)
        db.session.add(newUser)
        db.session.commit()
        return redirect(url_for("login"))

@app.route("/game/<game_id>/")
def game(game_id=None):
    if "logged_in" in session:
        if game_id:
            game = db.session.query(Game).get(game_id)
            return render_template("game.html", game=game, loggedIn = True)

        return abort(404)



# CLI Commands
@app.cli.command("initdb")
def init_db():
    """Initializes database and any model objects necessary for assignment"""
    db.drop_all()
    db.create_all()

    print("Initialized Connect 4 Database.")


@app.cli.command("devinit")
def init_dev_data():
    """Initializes database with data for development and testing"""
    db.drop_all()
    db.create_all()
    print("Initialized Connect 4 Database.")

    g = Game()
    db.session.add(g)

    g2 = Game()
    db.session.add(g2)

    p1 = Player(username="tow", password = "0000", birthday=datetime.datetime.strptime('11/06/1991', '%m/%d/%Y').date())
    p2 = Player(username="twaits", password = "0000", birthday=datetime.datetime.strptime('01/14/1987', '%m/%d/%Y').date())
    p3 = Player(username="pie", password = "0000", birthday=datetime.datetime.strptime('01/15/1987', '%m/%d/%Y').date())


    db.session.add(p1)
    print("Created %s" % p1.username)
    db.session.add(p2)
    print("Created %s" % p2.username)
    db.session.add(p3)
    print("Created %s" % p3.username)

    g.player_one = p1
    g.player_two = p2

    g.turn = 7
    g.winner_id = 1
    g.creator_id = 2

    g2.player_one = p1
    g2.player_two = p3

    g2.turn = 11
    g2.winner_id = 3
    g2.creator_id = 1
    
    db.session.commit()
    print("Added dummy data.")


if __name__ == "__main__":
    app.run(threaded=True)
