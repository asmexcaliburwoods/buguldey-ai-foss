uses dos,crt;

const
  hours = 10;
  minutes = 30;

var h,m,s,ss:word;
 t: longint;
 kb: Boolean;

begin
  ClrScr;
  writeln('Alarm set to ', hours, ':', minutes, '.');
  WriteLn('Press something to stop.'^M^J);
  repeat
    gettime(h,m,s,ss);
    {    Hour 0..23, Minute 0..59, Second 0..59,
         and Sec100 (hundredths of seconds) 0..99.
    }
    write(^M, h:2, ':', m:2, ':', s, ' ');

    t:= h*60+m;
    kb:= KeyPressed;

  until (t >= hours*60+minutes) or kb;

  if not(kb) then
  begin
    repeat
      for h:=50 to 3000 do
      begin
        Sound(h);
        for t:=0 to 100 do s:=0
      end
    until KeyPressed;
    NoSound;
    ReadKey
  end

end.