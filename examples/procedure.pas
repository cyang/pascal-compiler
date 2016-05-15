program procedureProgram;
var x, y, z: integer;

procedure p;
    begin
        z := x+y;
    end;

begin
    x := 1;
    y := 2;
    p;
    writeln(z);
end.