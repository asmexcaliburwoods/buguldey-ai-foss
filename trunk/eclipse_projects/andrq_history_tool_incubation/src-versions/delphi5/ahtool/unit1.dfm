object Form1: TForm1
  Left = 337
  Top = 103
  BorderIcons = [biSystemMenu, biMinimize]
  BorderStyle = bsSingle
  Caption = '&RQ Tools - History Merge Wizard'
  ClientHeight = 614
  ClientWidth = 669
  Color = clBtnFace
  Font.Charset = DEFAULT_CHARSET
  Font.Color = clWindowText
  Font.Height = -11
  Font.Name = 'MS Sans Serif'
  Font.Style = []
  Menu = MainMenu1
  OldCreateOrder = False
  PixelsPerInch = 96
  TextHeight = 13
  object Panel1: TPanel
    Left = 0
    Top = 0
    Width = 665
    Height = 329
    BevelOuter = bvNone
    TabOrder = 2
    object Label1: TLabel
      Left = 8
      Top = 112
      Width = 159
      Height = 13
      Caption = '&Enter main &&RQ installation folder:'
      FocusControl = EditDestinationFolder
    end
    object EditDestinationFolder: TEdit
      Left = 8
      Top = 128
      Width = 521
      Height = 21
      TabOrder = 0
      Text = 'C:\tmp\xxxx\andrq\'
      OnChange = EditDestinationFolderChange
    end
    object ButtonBrowseForDestFolder: TButton
      Left = 536
      Top = 128
      Width = 121
      Height = 25
      Caption = 'Bro&wse'
      TabOrder = 1
      OnClick = ButtonBrowseForDestFolderClick
    end
    object Panel2: TPanel
      Left = 0
      Top = 0
      Width = 665
      Height = 329
      BevelOuter = bvNone
      TabOrder = 2
      Visible = False
      object LabelSourceFolders: TLabel
        Left = 8
        Top = 16
        Width = 250
        Height = 13
        Caption = '&Source &&RQ installation folders to merge/sort by time:'
      end
      object SourceFolders: TListBox
        Left = 8
        Top = 32
        Width = 521
        Height = 153
        ItemHeight = 13
        Items.Strings = (
          'D:\system\gl\BINWIN\andrq\')
        TabOrder = 0
        OnClick = SourceFoldersClick
      end
      object ButtonAdd: TButton
        Left = 536
        Top = 32
        Width = 121
        Height = 25
        Caption = '&Add'
        TabOrder = 1
        OnClick = ButtonAddClick
      end
      object ButtonRemove: TButton
        Left = 536
        Top = 64
        Width = 121
        Height = 25
        Caption = '&Remove'
        Enabled = False
        TabOrder = 2
        OnClick = ButtonRemoveClick
      end
      object Panel3: TPanel
        Left = 0
        Top = 0
        Width = 665
        Height = 329
        BevelOuter = bvNone
        TabOrder = 3
        Visible = False
      end
    end
  end
  object GroupBoxProgress: TGroupBox
    Left = 40
    Top = 352
    Width = 673
    Height = 65
    Caption = 'Progress'
    TabOrder = 0
    Visible = False
    object LabelProgress: TLabel
      Left = 8
      Top = 24
      Width = 30
      Height = 13
      Caption = 'Status'
    end
    object ProgressBar1: TProgressBar
      Left = 8
      Top = 40
      Width = 655
      Height = 15
      Min = 0
      Max = 100
      Smooth = True
      TabOrder = 0
    end
  end
  object FileMemo: TMemo
    Left = 0
    Top = 376
    Width = 673
    Height = 233
    Lines.Strings = (
      'FileMemo')
    TabOrder = 1
  end
  object GroupBox1: TGroupBox
    Left = 8
    Top = 329
    Width = 657
    Height = 7
    TabOrder = 3
  end
  object PanelBackNext: TPanel
    Left = 0
    Top = 336
    Width = 673
    Height = 41
    BevelOuter = bvNone
    TabOrder = 4
    object BackBtn: TButton
      Left = 416
      Top = 8
      Width = 75
      Height = 25
      Caption = '&Back'
      Enabled = False
      TabOrder = 0
      OnClick = BackBtnClick
    end
    object NextBtn: TButton
      Left = 496
      Top = 8
      Width = 75
      Height = 25
      Caption = '&Next'
      TabOrder = 1
      OnClick = NextBtnClick
    end
    object Button1: TButton
      Left = 584
      Top = 8
      Width = 75
      Height = 25
      Caption = '&Finish'
      Enabled = False
      TabOrder = 2
    end
  end
  object MainMenu1: TMainMenu
    Left = 632
    Top = 8
    object File1: TMenuItem
      Caption = '&File'
      object Exit1: TMenuItem
        Caption = '&Exit'
        OnClick = Exit1Click
      end
    end
    object Help1: TMenuItem
      Caption = '&Help'
      object About1: TMenuItem
        Caption = '&About'
        OnClick = About1Click
      end
    end
  end
  object OpenDialogAdd: TOpenDialog
    FileName = 'file name ignored'
    Options = [ofHideReadOnly, ofPathMustExist, ofNoTestFileCreate, ofNoNetworkButton, ofEnableSizing]
    Title = 'Choose one of source &RQ installation folders'
    Left = 568
    Top = 8
  end
  object SaveDialogDestFolder: TSaveDialog
    Left = 600
    Top = 8
  end
end
