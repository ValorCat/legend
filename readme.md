# Legend
Legend is a WIP multi-purpose programming language that is intended to be intuitive and easy to learn.

Here's an example of a program that reads and stores movie ratings across sessions:
```
movie = type(name, rating: 1 to 5)
movies = autosave(list())
repeat
    "Enter command: {input = read()}"
    match input
        'best': movies max(by: _.rating) show
        'exit': exit()
        'list': movies show
        'rate': movies read
        else: "Commands: best, exit, list, or rate."
    end
end
```
