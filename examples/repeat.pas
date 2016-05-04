program repeatProgram;
var x: integer;

begin
    x := 0;
    repeat
        writeln(x);
        x := x + 1;
    until x = 10;
end.