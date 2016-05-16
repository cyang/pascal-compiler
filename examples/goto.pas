program gotoProgram;
label label1;
var x: integer;


begin
    x := 2;
    if x = 2 then
        goto label1;

    x := 3;

    label1: writeln(x);

end.
