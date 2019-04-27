from neo4j import GraphDatabase, basic_auth

# connection with authentication
# driver = GraphDatabase.driver("bolt://localhost:7687", auth=basic_auth("neo4j", "cs1656"), encrypted=False)

# connection without authentication
driver = GraphDatabase.driver("bolt://localhost:7687", encrypted=False)

session = driver.session()
transaction = session.begin_transaction()

with open('output.txt', 'w', encoding='utf-8') as output_file:

    ## Q1 ##

    result = transaction.run('''MATCH (a:Actor)-[:ACTS_IN]->(m:Movie) 
    WITH a, count(m) as movies_count
    RETURN a.name AS actor_name, movies_count 
    ORDER BY movies_count DESC 
    LIMIT 20''')

    output_file.write("### Q1 ###\n")

    for record in result:
        output_file.writelines("{}, {}\n".format(record['actor_name'], record['movies_count']))

    ##Q2 ##

    result = transaction.run('''MATCH (p:Person)-[rel:RATED]->(m:Movie) 
    WHERE rel.stars <= 3
    RETURN m.title as movie_title''')

    output_file.write("\n### Q2 ###\n")

    for record in result:
        output_file.writelines("{}\n".format(record['movie_title']))

    ## Q3 ##

    result = transaction.run('''MATCH ()-[rel:RATED]->(m:Movie)<-[:ACTS_IN]-(a:Actor)
    WITH m, count(distinct a) AS cast_number
    RETURN m.title as movie_title, cast_number
    ORDER BY cast_number DESC
    LIMIT 1''')

    output_file.write("\n### Q3 ###\n")

    for record in result:
        output_file.writelines("{}, {}\n".format(record['movie_title'], record['cast_number']))

    ## Q4 ##

    result = transaction.run('''MATCH (d:Director)-[:DIRECTED]->(m:Movie)<-[:ACTS_IN]-(a:Actor)
    WITH a, count(distinct d) AS director_number
    WHERE director_number >= 3
    RETURN a.name AS actor_name, director_number''')

    output_file.write("\n### Q4 ###\n")

    for record in result:
        output_file.writelines("{}, {}\n".format(record['actor_name'], record['director_number']))

    ## Q5 ##

    result = transaction.run('''MATCH (bacon:Actor {name: 'Kevin Bacon'})-[:ACTS_IN]->(m:Movie)<-[:ACTS_IN]-(bacon1:Actor)
    MATCH (bacon1:Actor)-[:ACTS_IN]->(m2:Movie)<-[:ACTS_IN]-(bacon2:Actor)
    WHERE bacon2 <> bacon AND NOT (bacon2)-[:ACTS_IN]->()<-[:ACTS_IN]-(bacon)
    RETURN bacon2.name AS actor_name''')

    output_file.write("\n### Q5 ###\n")

    for record in result:
        output_file.writelines("{}\n".format(record['actor_name']))

    ## Q6 ##

    result = transaction.run('''MATCH (a:Actor {name: 'Tom Hanks'})-[:ACTS_IN]->(m:Movie)
    RETURN distinct m.genre AS genre''')

    output_file.write("\n### Q6 ###\n")

    for record in result:
        output_file.writelines("{}\n".format(record['genre']))

    ## Q7 ##

    result = transaction.run('''MATCH (d:Director)-[:DIRECTED]->(m:Movie)
    WITH d, count(distinct m.genre) AS genre_number
    WHERE genre_number >= 2
    RETURN d.name AS director_name, genre_number''')

    output_file.write("\n### Q7 ###\n")

    for record in result:
        output_file.writelines("{}, {}\n".format(record['director_name'], record['genre_number']))

    ## Q8 ##
    result = transaction.run('''MATCH (d:Director)-[:DIRECTED]->(m:Movie)<-[:ACTS_IN]-(a:Actor)
    WITH d, a, count(m) as frequency
    RETURN d.name AS director_name, a.name AS actor_name, frequency
    ORDER BY frequency DESC
    LIMIT 5''')

    output_file.write("\n### Q8 ###\n")

    for record in result:
        output_file.writelines("{}, {}, {}\n".format(record['director_name'], record['actor_name'], record['frequency']))


transaction.close()
session.close()