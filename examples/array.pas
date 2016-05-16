program arrayProgram;
var a1: array[20..30] of integer;

begin
    a1[23] := 3;
    writeln(a1[23]);

    a1[24] := a1[23] + 5;
    writeln(a1[24]);
end.
