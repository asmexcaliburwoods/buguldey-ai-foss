program ahtool;
{ todo:
���������� � ������.  ���������� ���������������� ���:
1) ������� �������� ������� ����������� &RQ
2) �������� ������ ��������� &RQ ��� �������
   � �������� ������� &RQ

   // main install fld of &RQ is internally
      appended to this list
4) ������� ��������� ����� ��������� �������� &RQ?
   [default: yes]
5) ������ ��������� ������.
   ������� ������� ����� ������ �����.
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
