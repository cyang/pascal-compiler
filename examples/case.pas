program caseProgram;
var grade, num: integer;

begin
    grade := 100;

    case (grade) of
    100 : writeln();
    90, 'C': writeln('Well done');
    80 : writeln('You passed');
    70 : writeln('Better try again');
    end;

    writeln('Your grade is ', grade );
end.