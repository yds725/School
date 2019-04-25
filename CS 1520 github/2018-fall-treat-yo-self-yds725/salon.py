from flask import Flask, request, url_for, redirect, session, render_template, flash, abort
from flask_sqlalchemy import SQLAlchemy
from sqlalchemy import exc, and_ #exception class
import os
import datetime


from models import db, Stylist, Patron, Appoint

app = Flask(__name__)
app.static_folder = 'static'

app.config.update(dict(
	DEBUG=True,
	SECRET_KEY='Inefficient HTML Template',
	USERNAME='owner',
	PASSWORD='pass',
	SQLALCHEMY_DATABASE_URI = 'sqlite:///' + os.path.join(app.root_path, 'salon.db')
))

#app.config["SQLALCHEMY_DATABASE_URI"] = "sqlite:///salon.db"
#app.config["SECRET_KEY"] = "whatever"
#db = SQLAlchemy(app)

db.init_app(app) 

#initialize database for owner with admin privilege
@app.cli.command("initdb")
def initialize_db():
    db.drop_all() # clean db?
    db.create_all() # creates database tables
    owner = Stylist(firstname="Panther", lastname="Pitt", username="owner", password = "pass", admin_privilege = True)
    db.session.add(owner)
    db.session.commit()
    print("Database Initialized!")

@app.cli.command("bootstrap")
def bootstrap_db():
    db.drop_all()
    db.create_all()

    owner = Stylist(firstname="Panther", lastname="Pitt", username="owner", password = "pass", admin_privilege = True)
    db.session.add(owner)
    
    patron1 = Patron(username = "Patron1", password = "0000")
    db.session.add(patron1)
    patron2 = Patron(username = "Patron2", password = "0000")
    db.session.add(patron2)
    patron3 = Patron(username = "Patron3", password = "0000")
    db.session.add(patron3)
    db.session.commit()
    print("Added Patrons 1,2,3")

    stylist1 = Stylist(firstname = "John", lastname = "Penn", username="Stylist1", password = "0000", admin_privilege = False)
    db.session.add(stylist1)
    print("Added Stylist 1")

    stylist2 = Stylist(firstname = "Sherlock", lastname = "Holmes", username="Stylist2", password = "0000", admin_privilege = False)
    db.session.add(stylist2)
    print("Added Stylist 2")

    stylist3 = Stylist(firstname = "James", lastname = "Watt", username="Stylist3", password = "0000", admin_privilege = False)
    db.session.add(stylist3)
    print("Added Stylist 3")
    db.session.commit()
    date1 = datetime.date(2018,11,11)
    time1 = "2PM"

    date2 = datetime.date(2018,11,12)
    time2 = "10AM"

    date3 = datetime.date(2018,11,18)
    time3 = "7PM"

    appoint1 = Appoint(date = date1, time = time1, stylist_id = stylist1.id, patron_id = patron1.id)
    appoint2 = Appoint(date = date2, time = time2, stylist_id = stylist1.id, patron_id = patron1.id)
    appoint3 = Appoint(date = date3, time = time3, stylist_id = stylist2.id, patron_id = patron2.id)
    appoint4 = Appoint(date = date2, time = time2, stylist_id = stylist3.id, patron_id = patron3.id)
    appoint5 = Appoint(date = date3, time = time3, stylist_id = stylist3.id, patron_id = patron3.id)

    db.session.add(appoint1)
    db.session.add(appoint2)
    db.session.add(appoint3)
    db.session.add(appoint4)
    db.session.add(appoint5)
    print("Added appointments 1,2,3,4,5")
    db.session.commit()
    print("Created Appoints")

    print("Stylist id: " + str(appoint1.stylist_id))
    print("Patron username: " + appoint3.patron.username) #patron2
    print("Stylist name: " + appoint1.stylist.lastname) #Penn
    # patron1.appointments.append(appoint1)
    # print("Appended appoint to patron")
    # db.session.add(appoint1)
    # print("Added Event")
    # #set patron id to appoint
    # appoint1.patron = patron1
    # print("Set patron id to Appoint")
    # print("Patron username: " + appoint1.patron.username)
    # print("Stylist id: " + appoint1.stylist_id)
    # print("Real Stylist id: " + stylist1.id)
    db.session.commit()
    print("Initialized database!")
    
# Default Route
@app.route("/")
def default():
    return redirect(url_for("main"))

# Main page for Website where user can sigin, register new account for patrons
@app.route("/main/", methods = ["GET", "POST"])
def main():
    if(request.method == "GET"):
        if ("logged_in" in session):
            return render_template("main.html", logged = True)
        else:
            return render_template("main.html", logged = False)
    elif(request.method == "POST"):
        patron_user = Patron.query.filter_by(username = request.form["username"], password = request.form["password"]).scalar()
        stylist_user = Stylist.query.filter_by(username = request.form["username"], password = request.form["password"]).scalar()

        if patron_user != None:
            session["logged_in"] = request.form["username"]
            return redirect(url_for("profile_patron", username = session.get("logged_in")))
        elif stylist_user != None:
            session["logged_in"] = request.form["username"]
            return redirect(url_for("profile_stylist", username = session.get("logged_in")))
        else:
            flash("Username or Password is invalid")
            return render_template("main.html", logged = False)
    else:
        return render_template("main.html", logged = False)

# Register new account for patron 
@app.route("/new_patron/", methods = ["GET", "POST"])
def new_patron():
    if(request.method == "GET"):
        if ("logged_in" in session):
            return render_template("new_patron.html", logged = True)
        else:
            return render_template("new_patron.html", logged = False)
    
    elif(request.method == "POST"):
        if(Patron.query.filter_by(username = request.form["username"]).scalar() != None):
            flash("Duplicate Username. Please try again!")
            return redirect(url_for("new_patron"))
        elif request.form["password"] != request.form["password2"]:
            flash("Passwords does not match. Please check carefully!")
            return redirect(url_for("new_patron"))
        newPatron = Patron(username = request.form["username"], password = request.form["password"])
        db.session.add(newPatron)

        # may use try and except block
        db.session.commit()
        session["logged_in"] = request.form["username"]
        return redirect(url_for("profile_patron", username = session["logged_in"]))

@app.route("/new_stylist/", methods = ["GET", "POST"])
def new_stylist():
    if(request.method == "GET"):
        if(Stylist.query.filter_by(username = session["logged_in"], admin_privilege = True).scalar() == None):
            flash("No admin privilege! Only owner can register new account.")
            return redirect(url_for("main"))
        return render_template("new_stylist.html")
    
    elif(request.method == "POST"):
        if(Stylist.query.filter_by(username = request.form["username"]).scalar() != None):
            flash("Duplicate Stylist Username. Please try again!")
            return redirect(url_for(new_stylist))
        newStylist = Stylist(firstname = request.form["firstname"],
            lastname = request.form["lastname"], username = request.form["username"],
            password = request.form["password"], admin_privilege = False
        )
        db.session.add(newStylist)
        #may use try and catch block
        db.session.commit()
        flash("Profile is created for {}.".format(request.form["firstname"] + " " + request.form["lastname"]))
        return redirect(url_for("new_stylist"))

# this directs stylist, patron, owner user to their own profile page
# by clicking Proflie button at the top
@app.route("/redirect_profile/")
def redirect_profile():
    if("logged_in" in session):
        if(Stylist.query.filter_by(username = session["logged_in"]).scalar() != None):
            if(Stylist.query.filter_by(username = session["logged_in"], admin_privilege = True).scalar() != None):
                print("Go to Owner Profile page")
                return redirect(url_for("profile_owner", username = session["logged_in"]))
            else:
                print("Go to Stylist Profile page")
                return redirect(url_for("profile_stylist", username = session["logged_in"]))
        elif (Patron.query.filter_by(username = session["logged_in"]).scalar() != None ):
            print("Go to Patron Profile page")
            return redirect(url_for("profile_patron", username = session["logged_in"]))
        else:
            flash("The account does not exist. Cannot access to profile page!")
            return redirect(url_for("main"))
    else:
        flash("You need to sign in to access the profile page!")
        return redirect(url_for("main"))

    flash("Error occurred when accessing your profile page. Try again!")
    return redirect(url_for("main"))

@app.route("/stylist/<username>/")
def profile_stylist(username = None):
    if Stylist.query.filter_by(username = session["logged_in"]).scalar() is not None:
        if(Stylist.query.filter_by(username = session["logged_in"], admin_privilege = True).scalar() != None):
             return redirect(url_for("profile_owner", username = session["logged_in"]))
        else:
            stylist_page = Stylist.query.filter_by(username = session["logged_in"]).first()
            stylist_id = stylist_page.id
            return redirect(url_for("schedule_appointment", ID = stylist_id))
    else:
        flash("You must log in as stylist to access profile!")
        return redirect(url_for("default"))

@app.route("/patron/<username>/", methods = ["GET", "POST"])
def profile_patron(username = None):
    # this is for patron profile page
    if(request.method == "GET"):
        if(Patron.query.filter_by(username = session["logged_in"]).scalar() != None):
            patronQuery = Patron.query.filter_by(username = session["logged_in"]).first()
            date = datetime.datetime.now()
            newdate = date.replace(hour=0, minute=0, second=0)
            newdate = newdate.date()
            print (newdate)
            appointments_list = Appoint.query.order_by(Appoint.date).filter(Appoint.patron_id == patronQuery.id, Appoint.date >= newdate).all()
            # b_list = []
            # for a in appointments_list:
            #     appointed_stylist = Stylist.query.filter_by(id = a.stylist_id).first()
            #     stylist_fullname = appointed_stylist.firstname + " " + appointed_stylist.lastname
            #     new_entry = [a.id, stylist_fullname]
            #     b_list.append(new_entry)
            # print("BLABLA BLA!!!!")
            
    
            # ##

            # for a in appointments_list:
            #     specific_stylist = Stylist.query.filter_by(id = a.stylist_id).first()
            #     specific_stylist.username

            # stylistQuery = Stylist.query.filter_by(username = session["logged_in"]).first()
            # appointments_list = Appoint.query.order_by(Appoint.date).filter((Appoint.stylist == stylistQuery) 
            #     & (Appoint.date >= datetime.datetime.now())).all()

            # ##
            stylist_list = Stylist.query.order_by(Stylist.lastname).filter_by(admin_privilege = False).all()
            return render_template("patron.html", username = session.get("logged_in"), stylists = stylist_list, patron = patronQuery)
        else:
            flash("You are required to sign in to access profile page")
            return redirect(url_for("default"))
    # elif(request.method == "POST"):
    #     if("cancel_button" in request.form):
    #         patronQuery = Patron.query.filter_by(username = session.get("logged_in")).first()
    #         deleting_appoint = Appoint.query.filter((Appoint.stylist_id == request.form["cancel_button"]) & (Appoint.patron_id == patronQuery.id)).delete()

    #         db.session.commit()
    #         flash("The appointment was canceled")
    #         return redirect(url_for("profile_patron"), username = session.get("logged_in"))

@app.route("/patron/appointments/<USER>", methods = ["GET", "POST"])
def patron_appointment(USER):
    if request.method == "GET":
       patronQuery = Patron.query.filter_by(username = USER).first()
       date = datetime.datetime.now()
       newdate = date.replace(hour=0, minute=0, second=0)
       newdate = newdate.date()
       print (newdate)
       appointments_list = Appoint.query.order_by(Appoint.date).filter(Appoint.patron_id == patronQuery.id, Appoint.date >= newdate).all()

       if Patron.query.filter_by(username = session.get("logged_in")).scalar() is not None:
            patron_loggedIn = True
       else:
            patron_loggedIn = False
       
       return render_template("patron2.html", username = session.get("logged_in"), appointments = appointments_list, patron_loggedIn = patron_loggedIn)

    elif request.method == "POST":
       if("cancel_button" in request.form):
            patronQuery = Patron.query.filter_by(username = session.get("logged_in")).first()
            split_date = request.form["appointedDate"].split("-")
            appointDate = datetime.date(int(split_date[0]), int(split_date[1]), int(split_date[2]))

            cancel_day = request.form["appointedDate"]
            deleting_appoint = Appoint.query.filter_by(time = request.form["appointedTime"], patron_id = patronQuery.id).all()
            
            deleter = Appoint.query.filter( (and_((Appoint.date)==appointDate, Appoint.time==request.form["appointedTime"])) & (Appoint.patron_id == patronQuery.id)).delete()
            # for a in deleting_appoint:
            #     match_day = a.date.strftime('%Y-%m-%d')
            #     if match_day == cancel_day:
            #         a.query.filter_by(patron_id = patronQuery.id).delete()
            #     else:
            #         print("Not matching")
            

            print(deleting_appoint)
            print(request.form["appointedDate"])
            print(request.form["appointedTime"])

            db.session.commit()
            flash("The appointment was canceled")
            return redirect(url_for("profile_patron", username = session.get("logged_in")))


@app.route("/schedule/stylist_id/<ID>/", methods = ["GET", "POST"])
def schedule_appointment(ID):
    if(request.method == "GET"):
        if(Stylist.query.filter_by(id = ID).scalar() != None):
            
            appoint_list = Appoint.query.order_by(Appoint.date).filter(Appoint.stylist_id == ID).all()
            stylist_who = Stylist.query.filter_by(id = ID).first()

            row_dates = []
            col_dates =[]
            today = datetime.datetime.now()
            row_dates.append(today.strftime('%Y-%m-%d'))
            col_dates =[]
            col_dates.append(today.strftime('%a, %m/%d'))

            for i in range(7):
                today += datetime.timedelta(days=1)
                str_today = today.strftime('%Y-%m-%d')
                row_dates.append(str_today)
                col_dates.append(today.strftime('%a, %m/%d'))
            
            list1 = ["Book", "Book", "Book", "Book", "Book", "Book", "Book", "Book"]
            list2 = ["Book", "Book", "Book", "Book", "Book", "Book", "Book", "Book"]
            list3 = ["Book", "Book", "Book", "Book", "Book", "Book", "Book", "Book"]
            list4 = ["Book", "Book", "Book", "Book", "Book", "Book", "Book", "Book"]
            list5 = ["Book", "Book", "Book", "Book", "Book", "Book", "Book", "Book"]
            list6 = ["Book", "Book", "Book", "Book", "Book", "Book", "Book", "Book"]
            list7 = ["Book", "Book", "Book", "Book", "Book", "Book", "Book", "Book"]
            list8 = ["Book", "Book", "Book", "Book", "Book", "Book", "Book", "Book"]
            list9 = ["Book", "Book", "Book", "Book", "Book", "Book", "Book", "Book"]
            list10 = ["Book", "Book", "Book", "Book", "Book", "Book", "Book", "Book"]

            for a in appoint_list:
                if "10AM" == a.time:
                    for i, d in enumerate(row_dates):
                        if d == (a.date.strftime('%Y-%m-%d')):
                            list1[i] = "Not Available"
                        else:
                            list1[i] = "Book"
                elif "11AM" == a.time:
                    for i, d in enumerate(row_dates):
                        if d == (a.date.strftime('%Y-%m-%d')):
                            list2[i] = "Not Available"
                        else:
                            list2[i] = "Book"
                elif "12PM" == a.time:
                    for i, d in enumerate(row_dates):
                        if d == (a.date.strftime('%Y-%m-%d')):
                            list3[i] = "Not Available"
                        else:
                            list3[i] = "Book"
                elif "1PM" == a.time:
                    for i, d in enumerate(row_dates):
                        if d == (a.date.strftime('%Y-%m-%d')):
                            list4[i] = "Not Available"
                        else:
                            list4[i] = "Book"
                elif "2PM" == a.time:
                    for i, d in enumerate(row_dates):
                        if d == (a.date.strftime('%Y-%m-%d')):
                            list5[i] = "Not Available"
                        else:
                            list5[i] = "Book"
                elif "3PM" == a.time:
                    for i, d in enumerate(row_dates):
                        if d == (a.date.strftime('%Y-%m-%d')):
                            list6[i] = "Not Available"
                        else:
                            list6[i] = "Book"
                elif "4PM" == a.time:
                    for i, d in enumerate(row_dates):
                        if d == (a.date.strftime('%Y-%m-%d')):
                            list7[i] = "Not Available"
                        else:
                            list7[i] = "Book"
                elif "5PM" == a.time:
                    for i, d in enumerate(row_dates):
                        if d == (a.date.strftime('%Y-%m-%d')):
                            list8[i] = "Not Available"
                        else:
                            list8[i] = "Book"
                elif "6PM" == a.time:
                    for i, d in enumerate(row_dates):
                        if d == (a.date.strftime('%Y-%m-%d')):
                            list9[i] = "Not Available"
                        else:
                            list9[i] = "Book"
                elif "7PM" == a.time:
                    for i, d in enumerate(row_dates):
                        if d == (a.date.strftime('%Y-%m-%d')):
                            list10[i] = "Not Available"
                        else:
                            list10[i] = "Book"
            
            print(list1)
            print(list10)
            #stylist(owner) is logged in
            print(row_dates)

            if Stylist.query.filter_by(username = session.get("logged_in")).scalar() is not None:
                stylist_loggedIn = True
            else:
                stylist_loggedIn = False
            
            return render_template("stylist.html", username = session.get("logged_in"), stylist = stylist_who, col_dates = col_dates, row_dates = row_dates, list1 = list1, list2 = list2, list3 = list3, list4=list4, list5=list5, list6=list6, list7=list7, list8=list8, list9=list9, list10=list10, stylist_loggedIn = stylist_loggedIn, appointments = appoint_list)
        else:
            flash("There is no stylist for this stylist id. Please select another stylist!")
            return redirect(url_for("default"))
    elif(request.method == "POST"):
        print(request.form["appointDate"])
        print(request.form["appointTime"])
        #print(request.form["appointTime2"])
        print("Successful")
        currentPatron = Patron.query.filter_by(username = session.get("logged_in")).first()
        split_date = request.form["appointDate"].split("-")
        appointDate = datetime.date(int(split_date[0]), int(split_date[1]), int(split_date[2]))
        appointTime = request.form["appointTime"]
        
        print(appointDate)
        print(appointTime)

        # Add appointment which stores Patron and Stylist in appoint table
        scheduled_appointment = Appoint(date = appointDate, time = appointTime, patron = currentPatron, stylist_id = ID)
        db.session.add(scheduled_appointment)
        db.session.commit()
        flash("The appointment is successfully created!")
        return redirect(url_for("profile_patron", username=session.get("logged_in")))

@app.route("/owner/", methods = ["GET", "POST"])
def profile_owner(username = "owner"):
    if  (Stylist.query.filter_by(username = session["logged_in"], admin_privilege = 1).scalar() != None):
        owner = Stylist.query.filter(Stylist.username.like(session.get("logged_in")), Stylist.admin_privilege.like(1)).first()
        stylist_list = Stylist.query.order_by(Stylist.lastname).filter_by(admin_privilege = False).all()
        patron_list = Patron.query.order_by(Patron.username).all()
        return render_template("owner.html", username = session.get("logged_in"), stylists = stylist_list, patrons = patron_list)
    else:
        flash("You do not have admin privilege to access this page!")
        return redirect(url_for("default"))

@app.route("/logout/")
def logout():
    session.pop("logged_in", None)
    flash("You have been logged out!")
    return redirect(url_for("default"))

if __name__ == "__main__":
    app.run()
