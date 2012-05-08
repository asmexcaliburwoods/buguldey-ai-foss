unit Unit1;

interface

uses
  Windows, Messages, SysUtils, Classes, Graphics, Controls, Forms, Dialogs,
  StdCtrls, Menus, CheckLst, ComCtrls, mxarrays, ExtCtrls;

var
  TemporaryFolder: string;
type
  EAHTEx=class(Exception)
    public
      error:string;
      constructor Create(error_:string);
  end;
  int=integer;
  datetime = double;// (1.0 = 1 day = 24h, delphi format)
  TMsg=record
    original_file_pos: int;
    event_type: int;
    extra_items: ^string;
    sender_uin: int;
    event_time: datetime;
    body: ^string;
  end;
  PMsg=^TMsg;

  TPMsgArray = class(TBaseArray)
  public
    constructor Create(itemcount, dummy: Integer); override;
    function GetItem(index: Integer): PMsg;
    function Add(Value: PMsg): Integer;
    function Find(var Index: Integer; Value: PMsg): Boolean;
    procedure Assign(Source: TPersistent); override;
    property Items[Index: Integer]: PMsg read GetItem; default;
  end;

  TMsgArray=TPMsgArray;

  tbytes=array[0..0]of byte;
  pbytes=^tbytes;
  tchars=array[0..0]of char;
  pchars=^tchars;
  TForm1 = class(TForm)
    MainMenu1: TMainMenu;
    Exit1: TMenuItem;
    File1: TMenuItem;
    Help1: TMenuItem;
    About1: TMenuItem;
    GroupBoxProgress: TGroupBox;
    ProgressBar1: TProgressBar;
    LabelProgress: TLabel;
    OpenDialogAdd: TOpenDialog;
    FileMemo: TMemo;
    Panel1: TPanel;
    EditDestinationFolder: TEdit;
    SaveDialogDestFolder: TSaveDialog;
    ButtonBrowseForDestFolder: TButton;
    Label1: TLabel;
    GroupBox1: TGroupBox;
    PanelBackNext: TPanel;
    BackBtn: TButton;
    NextBtn: TButton;
    Button1: TButton;
    Panel2: TPanel;
    LabelSourceFolders: TLabel;
    SourceFolders: TListBox;
    ButtonAdd: TButton;
    ButtonRemove: TButton;
    Panel3: TPanel;
    procedure About1Click(Sender: TObject);
    procedure Exit1Click(Sender: TObject);
    procedure ButtonAddClick(Sender: TObject);
    procedure CheckListBox1Click(Sender: TObject);
    procedure CheckListBox1ClickCheck(Sender: TObject);
    procedure ButtonSetAsDestFolderClick(Sender: TObject);
    procedure SourceFoldersClick(Sender: TObject);
    procedure EditDestinationFolderChange(Sender: TObject);
    procedure EditTemporaryFolderChange(Sender: TObject);
    procedure ButtonBrowseForDestFolderClick(Sender: TObject);
    procedure ButtonMergeClick(Sender: TObject);
    procedure ButtonRemoveClick(Sender: TObject);
    procedure NextBtnClick(Sender: TObject);
    procedure BackBtnClick(Sender: TObject);
  private
    { Private declarations }
  public
    { Public declarations }
  end;

var
  Form1: TForm1;
const
  page: int=1;

implementation

{$R *.DFM}

function CmpMsg(var item1, item2): Integer;
var
  p: PMsg absolute item1;
  q: PMsg absolute item2;
begin
  //raise EArrayError.Create('not impl: CmpMsg');
  //Result := AnsiCompareStr(p1, p2);
//S1 > S2	>0
//S1 < S2	<0
//S1 = S2	=0
//TODO  if(p^.event_time>q^.event_time)return 1;
//TODO  if(p^.event_time<q^.event_time)return -1;
{TODO  ...

    event_type: int;
    extra_items: ^string;
    sender_uin: int;
    event_time: datetime;
    body: ^string;}
end;

procedure ArrayIndexError(Index: Integer);
begin
  raise EArrayError.Create('ArrayIndexOutOfRange: '+inttostr(Index));
end;


type
  TDArray = array[0..High(Integer) div Sizeof(PMsg)-1] of PMsg;
  PPMsgArray = ^TDArray;

constructor TPMsgArray.Create(itemcount, dummy: Integer);
begin
  inherited Create(itemcount, Sizeof(PMsg));
  CompareProc := CmpMsg;
end;

function TPMsgArray.GetItem(index: Integer): PMsg;
begin
  Result := nil;
  if ValidIndex(index) then
  begin
    try
      Result := PPMsgArray(List)^[index];
    except
      InternalHandleException;
    end;
  end;
end;

function TPMsgArray.Add(Value: PMsg): Integer;
begin
  if (SortOrder = tsNone) then
    Result := Count
  else
    if Find(Result, Value) then
      case Duplicates of
        dupIgnore : Exit;
        dupError  : raise EArrayError.Create('dup msg');
      end;
  InsertAt(Result, Value);
end;

procedure TPMsgArray.Assign(Source: TPersistent);
var
  I: Integer;  
begin
  if (Source is TPMsgArray) then
  begin
    try
      Clear;
      for I := 0 to TBaseArray(Source).Count - 1 do
        Add(TPMsgArray(Source)[I]);
    finally
    end;
    Exit;
  end;
  inherited Assign(Source);
end;

function TPMsgArray.Find(var Index: Integer; Value: PMsg): Boolean;
var
  L, H, I, C: Integer;
  Value2: PMsg; 
begin
  Result := False;
  L := 0;
  H := Count - 1;
  while (L <= H) do
  begin
    I := (L + H) shr 1;
    Value2 := GetItem(I);
    C := CompareProc(Value2, Value);
    if (C < 0) then
      L := I + 1
    else
    begin
      H := I - 1;
      if (C = 0) then
      begin
        Result := True;
        if (Duplicates <> dupAccept) then
          L := I;
      end;
    end;
  end;
  Index := L;
end;

procedure decritt(var s:string; key:integer);
asm
    mov ecx, key
    mov dl, cl
    shr ecx, 20
    mov dh, cl

    mov esi, s
    mov esi, [esi]
    or  esi, esi    // nil string
    jz  @OUT
    mov ah, 10111000b

    mov ecx, [esi-4]
    or  ecx, ecx
    jz  @OUT
  @IN:
    mov al, [esi]
    xor al, ah
    rol al, 3
    xor al, dh
    sub al, dl
  
    mov [esi], al
    inc esi
    ror ah, 3
    dec ecx
    jnz @IN
  @OUT:
end; // decritt

var status:string;

procedure setstatus(status_: string);
var prefix: string;
begin
  status:=status_;
  if form1.ProgressBar1.Position=0 then
  begin
    prefix:='';
    form1.ProgressBar1.Visible:=false;
  end
  else prefix:=inttostr(form1.ProgressBar1.position)+'% ';
  form1.LabelProgress.Caption:=prefix+status_;
  form1.GroupBoxProgress.update;
end;

procedure setprogress(percents: integer);
begin
  form1.ProgressBar1.Position:=percents;
  setstatus(status);
end;

procedure TForm1.About1Click(Sender: TObject);
begin
  MessageDlg('AHTOOL: &&RQ History Tool.  Site: http://ahtool.sf.net', mtInformation, [mbOK], 0);
end;

procedure error(s:string; b:boolean=true);
begin
  setstatus('Stopped on error: '+s);
  if b then MessageDlg(s, mtError, [mbOK], 0);
end;

procedure msg(s:string);
begin
  if MessageDlg(s, mtInformation, [mbOK,mbCancel], 0)=
    mrCancel then raise eahtex.create('[Cancel] selected');
end;

procedure TForm1.Exit1Click(Sender: TObject);
begin
  Close;
end;

procedure checkSetAsAndRemoveButtons;
var b: boolean;
begin
  b:= form1.SourceFolders.ItemIndex>=0;
  form1.ButtonRemove.Enabled:= b;
end;

procedure checkButtonMergeEnabled;
begin
  form1.NextBtn.Enabled:=(form1.SourceFolders.Items.Count>=1)
    and (form1.EditDestinationFolder.Text<>'');
end;

procedure TForm1.ButtonAddClick(Sender: TObject);
var i: integer;
begin
  //msg('OpenDialogAdd.InitialDir '''+OpenDialogAdd.InitialDir+'''');
  if SourceFolders.ItemIndex>=0 then begin
    OpenDialogAdd.files.clear;
    OpenDialogAdd.InitialDir:= SourceFolders.Items[SourceFolders.itemindex];
  end;
  OpenDialogAdd.filename:='file name ignored';
  if not OpenDialogAdd.Execute then exit;
  for i:=0 to OpenDialogAdd.Files.Count-1 do begin
    //msg('OpenDialogAdd.filepatg '''+ExtractFilePath(OpenDialogAdd.Files[i])+'''');
    SourceFolders.items.Add(ExtractFilePath(OpenDialogAdd.Files[i]));
  end;
  checkSetAsAndRemoveButtons;
  checkButtonMergeEnabled;
end;

procedure TForm1.CheckListBox1Click(Sender: TObject);
begin
  checkSetAsAndRemoveButtons;
end;

//const CheckListBox1eventFromUser:boolean=true;

procedure TForm1.CheckListBox1ClickCheck(Sender: TObject);
begin
  //if(CheckListBox1eventFromUser)then
  //  form1.CheckListBox1.Checked[
   ///     form1.CheckListBox1.ItemIndex]:=not
      //      form1.CheckListBox1.Checked[
        //       form1.CheckListBox1.ItemIndex];
end;

procedure TForm1.ButtonSetAsDestFolderClick(Sender: TObject);
begin
  EditDestinationFolder.Text:= SourceFolders.items[SourceFolders.itemindex];
  checkSetAsAndRemoveButtons;
end;

procedure TForm1.SourceFoldersClick(Sender: TObject);
begin
  checkSetAsAndRemoveButtons;
end;

procedure TForm1.EditDestinationFolderChange(Sender: TObject);
begin
  checkButtonMergeEnabled;
end;

procedure TForm1.EditTemporaryFolderChange(Sender: TObject);
begin
  checkButtonMergeEnabled;
end;

procedure TForm1.ButtonBrowseForDestFolderClick(Sender: TObject);
begin
  SaveDialogDestFolder.InitialDir:= EditDestinationFolder.Text;
  SaveDialogDestFolder.filename:='file name ignored';
  if not SaveDialogDestFolder.Execute then exit;
  EditDestinationFolder.Text:=
    ExtractFilePath(SaveDialogDestFolder.FileName);
end;

procedure verifyFolderIsWritable(fn: string);
var fs: TFileStream;
begin
  try
    fs := TFileStream.Create(fn+'\tmp.$$$',  //todo bad name i use
      fmCreate or fmOpenWrite);
    fs.Destroy;
  finally
    try
      DeleteFile(fn+'\tmp.$$$');
    except
    end;
  end;
end;

constructor EAHTEx.Create(error_:string);
begin
  error:= error_;
end;

function strtouin(suin:string):int64;
var uin: int64;
begin
  if(length(suin)=0)
    or(suin[4]<'0')or(suin[4]>'9')then
  begin strtouin:=0; exit; end;

  try
    uin:= strtoint64(suin);
  except on EConvertError do begin strtouin:=0; exit; end;
  end;
  if (uin<10000) or (uin>= int64(256)*256*256*256) then
  begin strtouin:=0; exit; end;
  strtouin:=uin;
end;

type
  {plocauindirlist=^tchatloglist;
  tchatloglist=record
    uindir:ppath;
    //uin:int64;
    next:pchatloglist;
  end;}
  premoteuinlist=^tremoteuinlist;
  tremoteuinlist=record
    remoteuin:int64;
    next:premoteuinlist;
  end;
  plocaluinlist=^tlocaluinlist;
  tlocaluinlist=record
    localuin: int64;
    localuindir: string;
    remoteuins: premoteuinlist;
    next: plocaluinlist;
  end;
var
  top,last:plocaluinlist;
  lastremote:premoteuinlist;


procedure addchatlog(uindir: string; uin,uin2:int64);
var rec:plocaluinlist; rrec: premoteuinlist;
begin
  if(last=nil)or(last^.localuindir<>uindir)then
  begin
    new(rec);
    with rec^ do
    begin
      localuin:=uin;
      localuindir:=uindir;
      remoteuins:=nil;lastremote:=nil;
      next:=nil;
    end;
    if(last=nil)then top:=rec else last^.next:=rec;
    last:=rec;
  end;

  //last^.remoteuins.Append(inttostr(uin2)):::
  new(rrec);
  with rrec^ do begin remoteuin:=uin2; next:=nil; end;
  if(lastremote=nil)then last^.remoteuins:=rrec
  else lastremote^.next:=rrec;
  lastremote:=rrec;
end;

procedure addVerifyChatlog(var uindir: string; uin:int64; searchRecName: string);
var uin2: int64;
begin
  uin2:= strtouin(searchRecName);
  if(uin2=0)then exit;
  //todo readopen that file; it is safe to have it 0 length
  addchatlog(uindir,uin,uin2);
  //msg('chatlog '+inttostr(uin)+' '+inttostr(uin2)+' '''+uindir+'\'+searchRecName+'''');
end;

procedure addVerifyUinDirToList(dir: string; uin: int64);
var searchRec: TSearchRec;
begin
  try
    if FindFirst(dir+'\history\*.', 0, searchRec)=0 then
      addVerifyChatlog(dir,uin,searchRec.Name)
    else raise eahtex.create(''''+dir+''' doesn''t have history (chatlog) files, please remove it from the source folders list');
    while FindNext(searchRec)=0 do
      addVerifyChatlog(dir,uin,searchRec.Name);
  finally
    FindClose(searchRec);
  end;
end;

procedure handleVerifyUinDir(parentdir,dir: string);
var uin: int64;
begin
  uin:= strtouin(dir);
  if(uin=0)then exit;
  addVerifyUinDirToList(parentdir+'\'+dir, uin);
  //msg('dir '''+dir+'''');
end;

procedure verifySourceFolder(sf: string);
var searchRec: TSearchRec;
begin
  try
    if FindFirst(sf+'\*.', faDirectory, searchRec)=0 then
      //Label1.Caption := SearchRec.Name + ' is ' + IntToStr(SearchRec.Size) + ' bytes in size';
      handleVerifyUinDir(sf, searchRec.Name);//searchRec.FindData.cFileName
    while FindNext(searchRec)=0 do
      //Label1.Caption := SearchRec.Name + ' is ' + IntToStr(SearchRec.Size) + ' bytes in size';
      handleVerifyUinDir(sf, searchRec.Name);
  finally
    FindClose(searchRec);
  end;
  //todo if IsUinDirsListEmpty then
    //todo raise EAHTex.Create('No uin folders at source folder '''+sf+'''');
end;

var tmpfld: string;

procedure fullverifysourcefolders;
var i: integer;
begin
  //clearUinDirsList b
  top:=nil; last:=nil; //todo dealloc memory
  //e
  for i:= 0 to form1.SourceFolders.Items.Count-1 do
    verifySourceFolder(form1.SourceFolders.Items[i]);
  lastremote:=nil;last:=nil;
end;

const maxchunk=10240;
type tbuf=record
    len: int;
    ptr: int;//offset in buf
    buf: array[0..maxchunk-1]of byte;
  end;

procedure merge(
  filefrom, fileto: string;
  localuin, remoteuin: int64);
var
  FromF, ToF: file;
  NumRead, NumWritten: Integer;
  Buf: array[1..64*1024] of Char;
begin
  AssignFile(FromF, filefrom); Reset(FromF, 1);	{ Record size = 1 }
  AssignFile(ToF, fileto);
  if(fileexists(fileto)) then Reset(ToF, 1)	{ Record size = 1 }
  else rewrite(tof,1);
  Seek(ToF, FileSize(ToF));
  repeat BlockRead(FromF, Buf, SizeOf(Buf), NumRead);
         BlockWrite(ToF, Buf, NumRead, NumWritten);
  until (NumRead = 0) or (NumWritten <> NumRead);
  CloseFile(FromF); CloseFile(ToF);
end;

procedure readabuf(var ffrom: file; var ab: tbuf);
begin
  BlockRead(ffrom, ab.buf, sizeof(ab.Buf), ab.len);
  ab.ptr:=0;
end;

function hasmore(var ffrom: file; var ab: tbuf):boolean;
begin
  if ab.len>0 then hasmore:=true
  else
  if eof(ffrom)then hasmore:= false
  else begin
    readabuf(ffrom,ab);
    hasmore:=true;
  end
end;

procedure expect(b: boolean; errmsg: string); begin
  if not b then raise eahtex.create('expectation violated: '+errmsg+' expected');
end;

function hexn(i: int):char;
const h:string='0123456789abcdef';
begin
  hexn:= h[(i and 15)+1];
end;

function hexb(i:int):string;
begin
  hexb:=hexn(i shr 4)+hexn(i);
end;

function hexw(i:int):string;
begin
  hexw:=hexb(i shr 8)+hexb(i);
end;

function hextostr(i:int):string;
begin
  hextostr:='$'+hexw(i shr 16)+hexw(i);
end;

function readbyte(var ffrom: file; var ab: tbuf): byte; forward;

//does dec(len,4);inc(p,4);
function readint(var ffrom: file; var ab: tbuf): int;
type typ=int; ptyp = ^typ; var explen: int;
 vv:array[1..sizeof(typ)]of byte;
 v:typ absolute vv;
 i:int;
begin
  explen:=sizeof(typ);
  for i:=1 to explen do vv[i]:= readbyte(ffrom,ab);
  readint:= v;
  //form1.filememo.lines.append(hextostr(v));
end;
{ vv:array[1..sizeof(typ)]of byte;
 v:typ absolute vv;
 i:int;
begin
  explen:=sizeof(typ);
  for i:=1 to explen do vv[i]:= readbyte(ffrom,ab);
  readdatetime:= v;
}

function readchar(var ffrom: file; var ab: tbuf): char;
begin
  readchar:= char(readbyte(ffrom, ab));
end;

//does dec(len,4);inc(p,4);
function readstring(var ffrom: file; var ab: tbuf): string;
type typ=string; ptyp = ^typ;
var i, explen: int; {start: int;} s: string;
begin
  //start:= ab.ptr;
  explen:=readint(ffrom,ab);
  //form1.filememo.lines.append('ab.len: '+inttostr(ab.len));
  //expect(ab.len>=explen, 'len>='+inttostr(explen));
  SetLength(s, explen);
  for i:= 0 to explen-1 do
    s[i+1]:= readchar(ffrom,ab);
  readstring:=s;
end;

function readstring_crypted(var ffrom: file; var ab: tbuf; sender_uin: int): string;
var s: string;
begin
  s:= readstring(ffrom, ab);
  decritt(s, sender_uin);
  readstring_crypted:= s
end;

//does dec(len,4);inc(p,4);
function readbyte(var ffrom: file; var ab: tbuf): byte;
type typ=byte; ptyp = ^typ; var explen: int;
begin
  explen:=sizeof(typ);
  if ab.len=0 then
    if not hasmore(ffrom,ab) then
      expect(ab.len>=explen, 'len>='+inttostr(explen));
  readbyte:= ab.buf[ab.ptr];
  inc(ab.ptr,explen);
  dec(ab.len,explen);
end;

//does dec(len,4);inc(p,4);
function readdatetime(var ffrom: file; var ab: tbuf): datetime;
type typ=datetime; ptyp = ^typ; var explen: int;
 vv:array[1..sizeof(typ)]of byte;
 v:typ absolute vv;
 i:int;
begin
  explen:=sizeof(typ);
  for i:=1 to explen do vv[i]:= readbyte(ffrom,ab);
  readdatetime:= v;
end;

procedure sort(
  filefrom, fileto: string;
  localuin, remoteuin: int64);
var ffrom,fto: file;
    abuf: tbuf;
    what: int;
    //len: int absolute abuf.len;
    //p: pointer absolute abuf.ptr;
    event_type: byte;
    sender_uin: int;
    event_time: datetime;
    body: string; //(crypted)
    hashed_password: string;
    //following_data_length: int;
    //crypt_mode: byte;
    N, i: int;
    extra_info: array[1..maxchunk] of byte;
begin
  form1.filememo.lines.append('');
  form1.filememo.lines.append('FILE: '+filefrom);
  AssignFile(ffrom, filefrom); Reset(ffrom, 1);	{ Record size = 1 }
  AssignFile(fto, fileto);  Rewrite(fto, 1);	{ Record size = 1 }
  try
    abuf.len:=0;
    //form1.filememo.lines.clear;
    //while not eof(ffrom) do begin
    while hasmore(ffrom, abuf) do begin
      what:= readint(ffrom, abuf);//does dec(len,4);inc(p,4);
      case what of
      -1:// (event)
        begin
        event_type:= readbyte(ffrom,abuf);
        sender_uin:= readint(ffrom,abuf);
        event_time:= readdatetime(ffrom,abuf);
        N:= readint(ffrom,abuf);
        for i:= 1 to N do
          extra_info[i]:= readbyte(ffrom,abuf);
        body:= readstring_crypted(ffrom,abuf,sender_uin);//int(localuin));
        //form1.filememo.lines.append('sender '+inttostr(sender_uin)+^I'et '+inttostr(event_type)+^I'tm '+DateTimeToStr(event_time)+^I'body '''+body+'''');
        end;
      -2:// (hashed)
        hashed_password:= readstring(ffrom,abuf);
      -3:// (crypt-mode)
        begin
        raise eahtex.create('crypt-mode not implemented');//todo
        //int       following-data-length
        //byte      crypt-mode (0 = simple, 1 = key method #1)
        end;
      end;
      //todo append chunk to fto
    end;
  finally
    CloseFile(ffrom); CloseFile(fto);
  end;
end;


procedure eraseTmpDir;
var searchRec: TSearchRec;
begin
  try
    if FindFirst(tmpfld+'\*-*', 0, searchRec)=0 then
    begin
      if MessageDlg('OK to delete temp dir contents?'^M^J'Temp dir: '''+tmpfld+'''', mtInformation, [mbYes,mbNo], 0)=mrNo
        then raise eahtex.create('do not erase tmp dir');
      deletefile(searchRec.Name);
     while FindNext(searchRec)=0 do
       deletefile(searchRec.Name);
    end;
  finally
    FindClose(searchRec);
  end;
end;

{first, we merge input files into a subdir in the temp;
second, we sort & remove dups into a dest dir.}
procedure processInputFiles;
var lo:plocaluinlist;re:premoteuinlist;
begin
  eraseTmpDir;
  lo:=top;
  while lo<>nil do with lo^ do
  begin
    re:=remoteuins;
    while re<>nil do with re^ do
    begin
      //todo progress updates
      setstatus('Appending '+localuindir+'\history\'+inttostr(remoteuin)+ ' to tmp file...');
      merge(localuindir+'\history\'+inttostr(remoteuin), tmpfld+'\'+inttostr(localuin)+'-'+inttostr(remoteuin), localuin, remoteuin);
      re:= re^.next;
    end;
    lo:=lo^.next;
  end;
  lo:=top;
  while lo<>nil do with lo^ do
  begin
    re:=remoteuins;
    while re<>nil do with re^ do
    begin
      setstatus('Sorting '+localuindir+'\history\'+inttostr(remoteuin)+'...');
      sort(tmpfld+'\'+inttostr(localuin)+'-'+inttostr(remoteuin),
        tmpfld+'\dest '+inttostr(localuin)+'-'+inttostr(remoteuin)
        //todo form1.EditDestinationFolder.Text+'\'+inttostr(localuin)+'\history\'+inttostr(remoteuin)
        , localuin, remoteuin); //todo
      re:= re^.next;
    end;
    lo:=next;
  end;
  setstatus('Removing temp files');
  //removeall(tmpfld); //todo
end;

procedure TForm1.ButtonMergeClick(Sender: TObject);
var b:boolean; stage: integer;
begin
  b:=false;
  SourceFolders.Enabled:=b;
  ButtonAdd.Enabled:=b;
  EditDestinationFolder.Enabled:=b;
  ButtonBrowseForDestFolder.Enabled:=b;
  NextBtn.Enabled:=b;
  setprogress(1);
  setstatus('Processing');
  GroupBoxProgress.Show;
  stage:=0;

  try
    setstatus('Verifying input data');
    GroupBoxProgress.Update;
    stage:=1;
    tmpfld:=TemporaryFolder+'\xxxx';//+'\ahtool';//todo
    //removeall(tmpfld);//todo //we append data to files, so we need this
    //mkdirrecursive(tmpfld);//todo
    verifyFolderIsWritable(tmpfld);
    stage:=2;
    verifyFolderIsWritable(EditDestinationFolder.Text);
    stage:=3;
    fullVerifySourceFolders;
    setprogress(2);
    GroupBoxProgress.Update;

    stage:=4;
    processInputFiles;

    setprogress(100);
    setstatus('Done.');
  except
    on e: eahtex do
      error(e.error);
    on e: EFCreateError do
      case stage of
        1: error('Temp folder '''+TemporaryFolder+''' must be writable');
        2: error('Destination folder '''+EditDestinationFolder.Text+''' must be writable');
        else error('Internal error: unknown stage: '+inttostr(stage));
      end;
    on e: exception do
      error('Unknown error: '+e.ClassName+'/'+e.Message);
  end;

  b:=true;
  SourceFolders.Enabled:=b;
  ButtonAdd.Enabled:=b;
  EditDestinationFolder.Enabled:=b;
  //ButtonBrowseForDestFolder.Enabled:=b;
  //ButtonBrowseForTempFolder.Enabled:=b;
  NextBtn.Enabled:=b;
end;

procedure TForm1.ButtonRemoveClick(Sender: TObject);
begin
  SourceFolders.Items.Delete(SourceFolders.ItemIndex);
  checkSetAsAndRemoveButtons;
end;

function getpage(i: int): tpanel;
begin
  case i of
  1: getpage:=form1.panel1;
  2: getpage:=form1.panel2;
  3: getpage:=form1.panel3;
  end;
end;

procedure syncpage(old: int);
begin
  getpage(old).Hide;
  getpage(page).Visible:=true;
end;

procedure TForm1.NextBtnClick(Sender: TObject);
begin
  if(page<7) then
  begin
    inc(page);
    syncpage(page-1);
  end;
end;

procedure TForm1.BackBtnClick(Sender: TObject);
begin
  if (page>1) then
  begin
    dec(page);
    syncpage(page+1);
  end;
end;

end.
