# Legend
Legend is a WIP multi-purpose programming language that is intended to be intuitive and easy to learn.

Here's an example of a program that reads and stores movie ratings across sessions:
```
Movie = Type(name, rating: 1 to 5)
movies = #autosave List(of: Movie)
repeat
    match "Enter command: {String read}"
        'best': movies.max(by: _.rating) show
        'exit': System exit
        'list': movies show
        'rate': movies read
        else: "Commands: best, exit, list, or rate."
    end
end
```

Checkout the wiki for a language overview: [Link to Wiki](https://github.com/ValorCat/legend/wiki).