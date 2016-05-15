program repeatProgram;
var x: integer;

begin
    x := 0;
    writeln(x);
    repeat
        x := x + 1;
    until x >= 10;
    writeln(x);
end.