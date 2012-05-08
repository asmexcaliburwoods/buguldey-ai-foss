program ahtool;
{ todo:
ѕеределать в визард.  ѕеределать функциональность так:
1) введите основной каталог инсталл€ции &RQ
2) создайте список каталогов &RQ дл€ импорта
   в основной каталог &RQ

   // main install fld of &RQ is internally
      appended to this list
4) создать резервную копию основного каталога &RQ?
   [default: yes]
5) сейчас произойдЄт импорт.
   процесс импорта может зан€ть врем€.
6) importing... please wait.
7) import complete. Back/Finish.
}

uses
  Forms,
  unit1 in 'unit1.pas' {Form1};

{$R *.RES}

begin
  Application.Initialize;
  Application.CreateForm(TForm1, Form1);
  Application.Run;
end.
