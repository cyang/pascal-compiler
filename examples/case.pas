program caseProgram;
var gradeNumber: integer;
var gradeLetter: char;


begin
    gradeNumber := 80;

    case (gradeNumber) of
        100 : gradeLetter := 'a';
        90 : gradeLetter := 'b';
        80 : gradeLetter := 'c';
        70 : gradeLetter := 'd';
    end;

    writeln(gradeLetter);
end.