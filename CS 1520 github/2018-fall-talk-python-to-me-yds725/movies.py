# Daesang Yoon - day42@pitt.edu
# Talk to Python to Me
# Using list comprehension, decorator, etc..

class Media:
    def __init__(self, title):
        self.title = title

    def slug(self):
        title = self.title

        result = ''.join(c for c in title if c.isalnum() or c.isspace())
        slug_title = result.lower().replace(" ", "-")
        print(slug_title)
        print()

class Movie(Media):
    def __init__(self, title, year, director, runtime):
        super().__init__(title)
        self.year = year
        self.director = director
        self.runtime = runtime

    def slug(self):
        super().slug()

    def __repr__(self):
        return "<Movie: " + self.title + " " + self.director + " " + str(self.runtime) + " >"

    def __str__(self):
        return "(" + str(self.year) + ") " + self.title + " " + self.director + " " + str(self.runtime)

    def abbrev(self):
        movie_title = self.title
        #result = movie_title.lower().replace(" ","")
        result = ''.join(c for c in movie_title if c.isalnum()).lower()
        three_title = result[:3]
        print(three_title)
        print()


def tag(msg):
    def decorator(function):
        def wrapper(*args, **kwargs):
            print("=====\n%s\n=====" % msg)
            return function(*args, **kwargs)
        return wrapper
    return decorator


@tag("Movie Slug")
def slugs(movies):
    [title.slug() for title in movies]

@tag("Movie Abbr")
def abbr(movies):
    [title.abbrev() for title in movies]

@tag("Previous Year")
def before_year(movies, year):
    #[print(repr(movie)) for movie in movies if movie.year < year]
    [print(movie) for movie in movies if movie.year < year]
    print()
    [print(repr(movie)) for movie in movies if movie.year < year]


def main(movies):
    number = 1997
    print("Thanks for checking the local Movie Database!")
    slugs(movies)
    abbr(movies)
    before_year(movies, number)
    print("Thanks!")

if __name__ == '__main__':
    movie1 = Movie("Don't do~` it", 1992, "Chris", 150.5)
    movie2 = Movie("Messi is @&*Best[]<=> Goat!", 1999, "Chris", 160.1)
    movie3 = Movie("CR7!!^{|}() Rock", 1998, "Chris", 178.56)
    movie4 = Movie("Barcelona ?:;_Is %#/Best", 1991, "Chris", 177.56)
    movie5 = Movie("G.I Joe", 1990, "Chris", 183.49)
    movies = [movie1, movie2, movie3, movie4, movie5]
    main(movies)