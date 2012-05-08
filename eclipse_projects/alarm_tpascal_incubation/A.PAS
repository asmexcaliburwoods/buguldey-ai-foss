uses dos;

var h,m,s,ss:word;
 t: longint;

begin
  writeln('Alarm set to 8:30.');
  repeat
    gettime(h,m,s,ss);
    {    Hour 0..23, Minute 0..59, Second 0..59,
         and Sec100 (hundredths of seconds) 0..99.
    }
    write(^M, h:3, m:3, s:3, ' ');

    t:= h*60+m;

  until t >= 8*60+30;

  writeln;
end.