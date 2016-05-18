program average;
var x, total, count: integer;
var avg: real;
var a: array[0..5] of integer;

begin
    total := 0;
    count := 6;

    a[0] := 10;
    a[1] := 23;
    a[2] := 40;
    a[3] := 67;
    a[4] := 99;
    a[5] := 100;

    for x := 0 to 5 do
    begin
       total := total + a[x];
    end;

    avg := total/count;
    writeln(avg);
end.