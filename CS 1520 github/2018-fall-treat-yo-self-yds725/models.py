# appointment - one to many patron ; one to many stylist so that patron - stylist
from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

# appointment = db.Table('appointment', 
#     db.Column('patron_id', db.Integer, db.ForeignKey('patron.id')), # appointment.id?
#     db.Column('stylist_id', db.Integer, db.ForeignKey('stylist.id'))
#     )

class Patron(db.Model):
    id = db.Column(db.Integer, primary_key = True)
    username = db.Column(db.String(50), unique = True)
    password = db.Column(db.String(100))
    appointments = db.relationship('Appoint', backref="patron", lazy = True)
    #stylists = db.relationship('Stylist', secondary = "Appoint", backref = "patrons", lazy = True)

    def __repr__(self):
        return "[Patron {}]".format(repr(self.username))

class Stylist(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    firstname = db.Column(db.String(50))
    lastname = db.Column(db.String(50))
    username = db.Column(db.String(50), unique = True)
    password = db.Column(db.String(100))
    admin_privilege = db.Column(db.Boolean)
    appointments = db.relationship('Appoint', backref="stylist", lazy = True)
    #appointment = db.relationship("Appoint", secondary = appointment, backref = appointments, lazy = "dynamic")

    def __repr__(self):
        return "[Stylist {}]".format(repr(self.username))

class Appoint(db.Model):
    id = db.Column(db.Integer, primary_key = True)
    date = db.Column(db.DateTime)
    time = db.Column(db.String(30))
    patron_id = db.Column(db.Integer, db.ForeignKey("patron.id"), nullable = False)
    stylist_id = db.Column(db.Integer, db.ForeignKey("stylist.id"), nullable = False)

    def __repr__(self):
        return "[Service {}]".format("haircut")