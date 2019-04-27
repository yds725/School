import sqlite3 as lite
import csv
import re
import os
import pandas as pd
import glob
from pprint import pprint

con = lite.connect('cs1656.sqlite')

with con:
	cur = con.cursor() 

	########################################################################		
	### CREATE TABLES ######################################################
	########################################################################		
	# DO NOT MODIFY - START 
	cur.execute('DROP TABLE IF EXISTS Actors')
	cur.execute("CREATE TABLE Actors(aid INT, fname TEXT, lname TEXT, gender CHAR(6), PRIMARY KEY(aid))")

	cur.execute('DROP TABLE IF EXISTS Movies')
	cur.execute("CREATE TABLE Movies(mid INT, title TEXT, year INT, rank REAL, PRIMARY KEY(mid))")

	cur.execute('DROP TABLE IF EXISTS Directors')
	cur.execute("CREATE TABLE Directors(did INT, fname TEXT, lname TEXT, PRIMARY KEY(did))")

	cur.execute('DROP TABLE IF EXISTS Cast')
	cur.execute("CREATE TABLE Cast(aid INT, mid INT, role TEXT)")

	cur.execute('DROP TABLE IF EXISTS Movie_Director')
	cur.execute("CREATE TABLE Movie_Director(did INT, mid INT)")
	# DO NOT MODIFY - END

	########################################################################		
	### READ DATA FROM FILES ###############################################
	########################################################################		
	# actors.csv, cast.csv, directors.csv, movie_dir.csv, movies.csv
	# UPDATE THIS

	current_dir = os.getcwd()
	files_path = os.path.join(current_dir, '*.csv')
	files_list = glob.glob(files_path)

	for each_csv in files_list:
		# df = pd.read_csv(each_csv)
		#
		# col_nums = len(df.columns)

		with open(each_csv, 'r', newline='') as file:
			csv_data = csv.reader(file)

			# cols = next(csv_data, None)
			file_name = os.path.basename(os.path.normpath(each_csv))
			sql_str = ""

			if file_name == 'actors.csv':
				sql_str = "INSERT INTO Actors VALUES(?, ?, ?, ?)"
				for row in csv_data:
					cur.execute(sql_str, row)

			elif file_name == 'movies.csv':
				sql_str = "INSERT INTO Movies VALUES(?, ?, ?, ?)"
				for row in csv_data:
					cur.execute(sql_str, row)

			elif file_name == 'cast.csv':
				sql_str = "INSERT INTO Cast VALUES(?, ?, ?)"
				for row in csv_data:
					cur.execute(sql_str, row)

			elif file_name == 'directors.csv':
				sql_str = "INSERT INTO Directors VALUES(?, ?, ?)"
				for row in csv_data:
					cur.execute(sql_str, row)

			elif file_name == 'movie_dir.csv':
				sql_str = "INSERT INTO Movie_Director VALUES(?, ?)"
				for row in csv_data:
					cur.execute(sql_str, row)

			# for row in csv_data:
			# 	cur.execute(sql_str, row)


	########################################################################		
	### INSERT DATA INTO DATABASE ##########################################
	########################################################################		
	# UPDATE THIS TO WORK WITH DATA READ IN FROM CSV FILES
	# cur.execute("INSERT INTO Actors VALUES(1001, 'Harrison', 'Ford', 'Male')")
	# cur.execute("INSERT INTO Actors VALUES(1002, 'Daisy', 'Ridley', 'Female')")
	#
	# cur.execute("INSERT INTO Movies VALUES(101, 'Star Wars VII: The Force Awakens', 2015, 8.2)")
	# cur.execute("INSERT INTO Movies VALUES(102, 'Rogue One: A Star Wars Story', 2016, 8.0)")
	#
	# cur.execute("INSERT INTO Cast VALUES(1001, 101, 'Han Solo')")
	# cur.execute("INSERT INTO Cast VALUES(1002, 101, 'Rey')")
	#
	# cur.execute("INSERT INTO Directors VALUES(5000, 'J.J.', 'Abrams')")
	#
	# cur.execute("INSERT INTO Movie_Director VALUES(5000, 101)")

	con.commit()
    
    	

	########################################################################
	### QUERY SECTION ######################################################
	########################################################################
	queries = {}

	# DO NOT MODIFY - START
	# DEBUG: all_movies ########################
	queries['all_movies'] = '''
SELECT * FROM Movies
'''
	# DEBUG: all_actors ########################
	queries['all_actors'] = '''
SELECT * FROM Actors
'''
	# DEBUG: all_cast ########################
	queries['all_cast'] = '''
SELECT * FROM Cast
'''
	# DEBUG: all_directors ########################
	queries['all_directors'] = '''
SELECT * FROM Directors
'''
	# DEBUG: all_movie_dir ########################
	queries['all_movie_dir'] = '''
SELECT * FROM Movie_Director
'''
	# DO NOT MODIFY - END

	########################################################################
	### INSERT YOUR QUERIES HERE ###########################################
	########################################################################
	# NOTE: You are allowed to also include other queries here (e.g.,
	# for creating views), that will be executed in alphabetical order.
	# We will grade your program based on the output files q01.csv,
	# q02.csv, ..., q12.csv

	cur.execute("DROP VIEW if exists movie_cast")
	cur.execute("DROP VIEW if exists movie_cast_actor")
	cur.execute("CREATE VIEW movie_cast AS SELECT m.*, c.role, c.aid FROM Movies AS m INNER JOIN Cast AS c ON m.mid = c.mid")
	cur.execute("CREATE VIEW movie_cast_actor AS SELECT * FROM Actors NATURAL JOIN movie_cast")
	# Q01 ########################
	# List all the actors (first and last name) who acted in at least one film in the 80s (1980-1990, both ends inclusive)
	# and in at least one film in the 21st century (>=2000). Sort alphabetically, by the actor's last and first name
	queries['q01'] = '''SELECT fname, lname FROM Actors 
	WHERE aid IN (SELECT aid FROM movie_cast_actor
	WHERE year between 1980 and 1990) AND 
	aid IN (SELECT aid FROM movie_cast_actor WHERE year >= 2000) 
	ORDER BY lname, fname 
'''

	# Q02 ########################
	#List all the movies (title, year) that were released in the same year as the movie entitled "Rogue One: A Star Wars Story",
	# but had a better rank (Note: the higher the value in the rank attribute, the better the rank of the movie).
	# Sort alphabetically, by movie title.
	queries['q02'] = '''SELECT title, year FROM Movies WHERE year = (SELECT year FROM Movies WHERE title = 'Rogue One: A Star Wars Story' LIMIT 1)
	AND rank > (SELECT rank FROM Movies WHERE title = 'Rogue One: A Star Wars Story' LIMIT 1)
	ORDER BY title ASC
'''


	# Q03 ########################
	#List all the actors (first and last name) who played in a Star Wars movie (i.e., title like '%Star Wars%')
	# in decreasing order of how many Star Wars movies they appeared in.
	# If an actor plays multiple roles in the same movie, count that still as one movie.
	# If there is a tie, use the actor's last and first name to generate a full sorted order.
	queries['q03'] = '''SELECT fname, lname FROM Actors as a, 
	(SELECT aid, count(DISTINCT mid) as num FROM movie_cast_actor WHERE title LIKE '%Star Wars%'
	GROUP BY aid) sm 
	WHERE a.aid = sm.aid
	ORDER BY sm.num DESC, lname, fname
'''

	# Q04 ########################
	# Find the actor(s) (first and last name) who only acted in films released before 1985.
	# Sort alphabetically, by the actor's last and first name.
	queries['q04'] = '''SELECT a.fname, a.lname FROM Actors AS a WHERE a.aid NOT IN (SELECT aid FROM movie_cast_actor WHERE year >= 1985)
	ORDER BY lname, fname
'''
	cur.execute("DROP VIEW if exists director_movies")
	cur.execute("DROP VIEW if exists movie_cast_actor_director")
	cur.execute("CREATE VIEW director_movies AS SELECT d.*, m.mid FROM Directors as d INNER JOIN Movie_Director as m ON d.did = m.did")
	cur.execute("CREATE VIEW movie_cast_actor_director AS SELECT d.did, d.fname as d_fname, d.lname as d_lname, m.* FROM director_movies as d INNER JOIN movie_cast_actor as m ON d.mid = m.mid")
	# Q05 ########################
	# List the top 20 directors in descending order of the number of films they directed (first name, last name, number of films directed).
	# For simplicity, feel free to ignore ties at the number 20 spot (i.e., always show up to 20 only)
	queries['q05'] = '''SELECT fname, lname, dir.film_counts FROM Directors as d, 
	(SELECT did, count(mid) as film_counts FROM director_movies
	GROUP BY did) dir
	WHERE d.did = dir.did
	ORDER BY dir.film_counts DESC
	LIMIT 20
'''

	# Q06 ######################## ??
	#  Find the top 10 movies with the largest cast (title, number of cast members) in decreasing order.
	# Note: show all movies in case of a tie.
	queries['q06'] = '''SELECT title, count(aid) as cast_num FROM movie_cast
	GROUP BY title
	HAVING cast_num IN (SELECT distinct cast_number FROM (SELECT count(aid) as cast_number FROM movie_cast
	GROUP BY mid
	ORDER BY cast_number DESC
	LIMIT 10))
	ORDER BY cast_num DESC
'''

	# Q07 ########################
	# Find the movie(s) whose cast has more actresses than actors (i.e., gender=female vs gender=male).
	# Show the title, the number of actresses, and the number of actors in the results. Sort alphabetically, by movie title.
	queries['q07'] = '''SELECT title,
	count(CASE WHEN gender='Female' THEN 1 END) as actress_num,
	count(CASE WHEN gender='Male' THEN 1 END) as actor_num
	FROM movie_cast_actor
	GROUP BY title
	HAVING actress_num > actor_num
	ORDER BY title
'''


	# Q08 ########################
	# Find all the actors who have worked with at least 7 different directors.
	# Do not consider cases of self-directing (i.e., when the director is also an actor in a movie),
	# but count all directors in a movie towards the threshold of 7 directors.
	# Show the actor's first, last name, and the number of directors he/she has worked with.
	# Sort in decreasing order of number of directors.
	queries['q08'] = '''SELECT fname, lname, count(distinct did) as num_directors
	FROM movie_cast_actor_director
	WHERE fname <> d_fname AND lname <> d_lname
	GROUP BY fname, lname
	HAVING num_directors >= 7
	ORDER BY num_directors DESC
'''

	# Q09 ########################
	# For all actors whose first name starts with an S, count the movies that he/she appeared in his/her debut year
	# (i.e., year of their first movie).
	# Show the actor's first and last name, plus the count. Sort by decreasing order of the count.
	cur.execute("DROP VIEW if exists actor_debut_year")
	cur.execute("CREATE VIEW actor_debut_year AS SELECT aid, min(year) as debut_year FROM movie_cast_actor WHERE fname LIKE 'S%' GROUP BY aid")
	queries['q09'] = '''SELECT fname, lname, debut.movie_count FROM Actors as a,(SELECT a.aid as aid, count(m.mid) as movie_count FROM movie_cast_actor as m, actor_debut_year as a
	WHERE m.aid = a.aid AND m.year = a.debut_year
	GROUP BY a.aid) debut
	WHERE a.aid = debut.aid
	ORDER BY debut.movie_count DESC
'''

	# Q10 ########################
	# Find instances of nepotism between actors and directors,
	# i.e., an actor in a movie and the director having the same last name, but a different first name.
	# Show the last name and the title of the movie, sorted alphabetically by last name.
	queries['q10'] = '''SELECT lname, title FROM movie_cast_actor_director
	WHERE lname = d_lname AND fname <> d_fname
	ORDER BY lname
'''

	# Q11 ########################
	# The Bacon number of an actor is the length of the shortest path between the actor and Kevin Bacon in the "co-acting" graph.
	# That is, Kevin Bacon has Bacon number 0; all actors who acted in the same movie as him have Bacon number 1;
	# all actors who acted in the same film as some actor with Bacon number 1 have Bacon number 2, etc.
	# List all actors whose Bacon number is 2 (first name, last name).
	cur.execute("DROP VIEW if exists bacon_people")
	cur.execute("DROP VIEW if exists bacon_number1")
	cur.execute("CREATE VIEW bacon_people AS SELECT aid FROM movie_cast_actor WHERE mid IN (SELECT mid FROM movie_cast_actor WHERE fname = 'Kevin' AND lname = 'Bacon') AND fname <> 'Kevin' AND lname <>'Bacon'")
	cur.execute("CREATE VIEW bacon_number1 AS SELECT mid FROM movie_cast WHERE aid IN (SELECT aid FROM bacon_people)")
	queries['q11'] = ''' SELECT fname, lname FROM Actors WHERE aid IN
	(SELECT aid FROM movie_cast WHERE mid IN (SELECT mid FROM bacon_number1) EXCEPT SELECT aid FROM bacon_people)
	 AND fname <> 'Kevin' AND lname <> 'Bacon'
'''

	# Q12 ########################
	# Assume that the popularity of an actor is reflected by the average rank of all the movies he/she has acted in.
	# Find the top 20 most popular actors (in descreasing order of popularity) --
	# list the actor's first/last name, the total number of movies he/she has acted, and his/her popularity score.
	# For simplicity, feel free to ignore ties at the number 20 spot (i.e., always show up to 20 only).
	queries['q12'] = '''SELECT fname, lname, count(mid) as movies_num, avg(rank) as popularity_score
	FROM movie_cast_actor
	GROUP BY fname, lname
	ORDER BY popularity_score DESC
	LIMIT 20
'''


	########################################################################
	### SAVE RESULTS TO FILES ##############################################
	########################################################################
	# DO NOT MODIFY - START
	for (qkey, qstring) in sorted(queries.items()):
		try:
			cur.execute(qstring)
			all_rows = cur.fetchall()

			print ("=========== ",qkey," QUERY ======================")
			print (qstring)
			print ("----------- ",qkey," RESULTS --------------------")
			for row in all_rows:
				print (row)
			print (" ")

			save_to_file = (re.search(r'q0\d', qkey) or re.search(r'q1[012]', qkey))
			if (save_to_file):
				with open(qkey+'.csv', 'w') as f:
					writer = csv.writer(f)
					writer.writerows(all_rows)
					f.close()
				print ("----------- ",qkey+".csv"," *SAVED* ----------------\n")

		except lite.Error as e:
			print ("An error occurred:", e.args[0])
	# DO NOT MODIFY - END
	
