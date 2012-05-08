//-----------------------------------------------------------------------------
// File: dxmodelist.cpp
//
// Desc: DirectX window application created by the DirectX AppWizard
//-----------------------------------------------------------------------------
#include "stdafx.h"
#include "DXUtil.h"
#include "D3DEnumeration.h"
#include "D3DSettings.h"
#include "D3DApp.h"
#include "D3DUtil.h"
#include "resource.h"
#include "dxmodelist.h"

const D3DFORMAT fmtArray[] = {     
  D3DFMT_R8G8B8,
  D3DFMT_A8R8G8B8,
  D3DFMT_X8R8G8B8,
  D3DFMT_R5G6B5,
  D3DFMT_X1R5G5B5,
  D3DFMT_A1R5G5B5,
  D3DFMT_A4R4G4B4,
  D3DFMT_R3G3B2,
  D3DFMT_A8,
  D3DFMT_A8R3G3B2,
  D3DFMT_X4R4G4B4,
  D3DFMT_A2B10G10R10,
  D3DFMT_A8B8G8R8,
  D3DFMT_X8B8G8R8,
  D3DFMT_G16R16,
  D3DFMT_A2R10G10B10,
  D3DFMT_A16B16G16R16,

  D3DFMT_A8P8,
  D3DFMT_P8,

  D3DFMT_L8,
  D3DFMT_A8L8,
  D3DFMT_A4L4,

  D3DFMT_V8U8,
  D3DFMT_L6V5U5,
  D3DFMT_X8L8V8U8,
  D3DFMT_Q8W8V8U8,
  D3DFMT_V16U16,
  D3DFMT_A2W10V10U10,

  D3DFMT_UYVY,
  D3DFMT_R8G8_B8G8,
  D3DFMT_YUY2,
  D3DFMT_G8R8_G8B8,
  D3DFMT_DXT1,
  D3DFMT_DXT2,
  D3DFMT_DXT3,
  D3DFMT_DXT4,
  D3DFMT_DXT5,

  D3DFMT_D16_LOCKABLE,
  D3DFMT_D32,
  D3DFMT_D15S1,
  D3DFMT_D24S8,
  D3DFMT_D24X8,
  D3DFMT_D24X4S4,
  D3DFMT_D16,

  D3DFMT_D32F_LOCKABLE,
  D3DFMT_D24FS8,


  D3DFMT_L16,

  D3DFMT_VERTEXDATA,
  D3DFMT_INDEX16,
  D3DFMT_INDEX32,

  D3DFMT_Q16W16V16U16,

  D3DFMT_MULTI2_ARGB8,

  // Floating point surface formats

  // s10e5 formats (16-bits per channel)
  D3DFMT_R16F,
  D3DFMT_G16R16F,
  D3DFMT_A16B16G16R16F,

  // IEEE s23e8 formats (32-bits per channel)
  D3DFMT_R32F,
  D3DFMT_G32R32F,
  D3DFMT_A32B32G32R32F,

  D3DFMT_CxV8U8
};
const UINT fmtArrayCount = sizeof(fmtArray) / sizeof(fmtArray[0]);

char* devTypeToString(D3DDEVTYPE devType){
  switch(devType){
    case D3DDEVTYPE_HAL: return "D3DDEVTYPE_HAL";
    case D3DDEVTYPE_SW: return "D3DDEVTYPE_SW";
    case D3DDEVTYPE_REF: return "D3DDEVTYPE_REF";
    default: return "D3DDEVTYPE_(UNKNOWN DEVICE TYPE)";
  }
}
char* fmtToString(D3DFORMAT fmt){
  switch(fmt){
    case D3DFMT_R8G8B8: return "D3DFMT_R8G8B8";
    case D3DFMT_A8R8G8B8: return "D3DFMT_A8R8G8B8";
    case D3DFMT_X8R8G8B8: return "D3DFMT_X8R8G8B8";
    case D3DFMT_R5G6B5: return "D3DFMT_R5G6B5";
    case D3DFMT_X1R5G5B5: return "D3DFMT_X1R5G5B5";
    case D3DFMT_A1R5G5B5: return "D3DFMT_A1R5G5B5";
    case D3DFMT_A4R4G4B4: return "D3DFMT_A4R4G4B4";
    case D3DFMT_R3G3B2: return "D3DFMT_R3G3B2";
    case D3DFMT_A8: return "D3DFMT_A8";
    case D3DFMT_A8R3G3B2: return "D3DFMT_A8R3G3B2";
    case D3DFMT_X4R4G4B4: return "D3DFMT_X4R4G4B4";
    case D3DFMT_A2B10G10R10: return "D3DFMT_A2B10G10R10";
    case D3DFMT_A8B8G8R8: return "D3DFMT_A8B8G8R8";
    case D3DFMT_X8B8G8R8: return "D3DFMT_X8B8G8R8";
    case D3DFMT_G16R16: return "D3DFMT_G16R16";
    case D3DFMT_A2R10G10B10: return "D3DFMT_A2R10G10B10";
    case D3DFMT_A16B16G16R16: return "D3DFMT_A16B16G16R16";

    case D3DFMT_A8P8: return "D3DFMT_A8P8";
    case D3DFMT_P8: return "D3DFMT_P8";

    case D3DFMT_L8: return "D3DFMT_L8";
    case D3DFMT_A8L8: return "D3DFMT_A8L8";
    case D3DFMT_A4L4: return "D3DFMT_A4L4";

    case D3DFMT_V8U8: return "D3DFMT_V8U8";
    case D3DFMT_L6V5U5: return "D3DFMT_L6V5U5";
    case D3DFMT_X8L8V8U8: return "D3DFMT_X8L8V8U8";
    case D3DFMT_Q8W8V8U8: return "D3DFMT_Q8W8V8U8";
    case D3DFMT_V16U16: return "D3DFMT_V16U16";
    case D3DFMT_A2W10V10U10: return "D3DFMT_A2W10V10U10";

    case D3DFMT_UYVY: return "D3DFMT_UYVY";
    case D3DFMT_R8G8_B8G8: return "D3DFMT_R8G8_B8G8";
    case D3DFMT_YUY2: return "D3DFMT_YUY2";
    case D3DFMT_G8R8_G8B8: return "D3DFMT_G8R8_G8B8";
    case D3DFMT_DXT1: return "D3DFMT_DXT1";
    case D3DFMT_DXT2: return "D3DFMT_DXT2";
    case D3DFMT_DXT3: return "D3DFMT_DXT3";
    case D3DFMT_DXT4: return "D3DFMT_DXT4";
    case D3DFMT_DXT5: return "D3DFMT_DXT5";

    case D3DFMT_D16_LOCKABLE: return "D3DFMT_D16_LOCKABLE";
    case D3DFMT_D32: return "D3DFMT_D32";
    case D3DFMT_D15S1: return "D3DFMT_D15S1";
    case D3DFMT_D24S8: return "D3DFMT_D24S8";
    case D3DFMT_D24X8: return "D3DFMT_D24X8";
    case D3DFMT_D24X4S4: return "D3DFMT_D24X4S4";
    case D3DFMT_D16: return "D3DFMT_D16";

    case D3DFMT_D32F_LOCKABLE: return "D3DFMT_D32F_LOCKABLE";
    case D3DFMT_D24FS8: return "D3DFMT_D24FS8";


    case D3DFMT_L16: return "D3DFMT_L16";

    case D3DFMT_VERTEXDATA: return "D3DFMT_VERTEXDATA";
    case D3DFMT_INDEX16: return "D3DFMT_INDEX16";
    case D3DFMT_INDEX32: return "D3DFMT_INDEX32";

    case D3DFMT_Q16W16V16U16: return "D3DFMT_Q16W16V16U16";

    case D3DFMT_MULTI2_ARGB8: return "D3DFMT_MULTI2_ARGB8";

            // Floating point surface formats

            // s10e5 formats (16-bits per channel)
    case D3DFMT_R16F: return "D3DFMT_R16F";
    case D3DFMT_G16R16F: return "D3DFMT_G16R16F";
    case D3DFMT_A16B16G16R16F: return "D3DFMT_A16B16G16R16F";

            // IEEE s23e8 formats (32-bits per channel)
    case D3DFMT_R32F: return "D3DFMT_R32F";
    case D3DFMT_G32R32F: return "D3DFMT_G32R32F";
    case D3DFMT_A32B32G32R32F: return "D3DFMT_A32B32G32R32F";

    case D3DFMT_CxV8U8: return "D3DFMT_CxV8U8";
    default: return "D3DFMT_(UNKNOWN FORMAT)";
  }
}

//-----------------------------------------------------------------------------
// Name: WinMain()
// Desc: Entry point to the program. Initializes everything, and goes into a
//       message-processing loop. Idle time is used to render the scene.
//-----------------------------------------------------------------------------
//INT WINAPI WinMain( HINSTANCE hInst, HINSTANCE, LPSTR, INT )
int main(){
  LPDIRECT3D9 m_pD3D = Direct3DCreate9( D3D_SDK_VERSION );
  UINT numAdapters = m_pD3D->GetAdapterCount();

  for (UINT adapterOrdinal = 0; adapterOrdinal < numAdapters; adapterOrdinal++)
  {
    cout<<"Adapter #"<<dec<<adapterOrdinal<<":"<<endl;
    D3DADAPTER_IDENTIFIER9 AdapterIdentifier;
    m_pD3D->GetAdapterIdentifier(adapterOrdinal, 0, &AdapterIdentifier);

    for( UINT iaaf = 0; iaaf < fmtArrayCount; iaaf++ ){
      D3DFORMAT allowedAdapterFormat = fmtArray[iaaf];
      UINT numAdapterModes = m_pD3D->GetAdapterModeCount( adapterOrdinal, allowedAdapterFormat );
      if(numAdapterModes>0){
        cout<<"  Format "<<fmtToString(allowedAdapterFormat)<<":"<<endl;
        for (UINT mode = 0; mode < numAdapterModes; mode++){
          D3DDISPLAYMODE displayMode;
          HRESULT hr=m_pD3D->EnumAdapterModes( adapterOrdinal, allowedAdapterFormat, mode, &displayMode );
          cout<<"    Mode "<<mode<<": "<<displayMode.Width<<"x"<<displayMode.Height<<
            ", "<<displayMode.RefreshRate<<"Hz, "<<fmtToString(displayMode.Format)<<endl;
          if(hr!=D3D_OK)cout<<"     EnumAdapterModes return value: "<<dec<<hr<<" (0x"<<hex<<hr<<")"<<endl;
        }
      }
    }
    
    const D3DDEVTYPE devTypeArray[] = { D3DDEVTYPE_HAL, D3DDEVTYPE_SW, D3DDEVTYPE_REF };
    const UINT devTypeArrayCount = sizeof(devTypeArray) / sizeof(devTypeArray[0]);
    HRESULT hr;
    D3DCAPS9 Caps;
    for( UINT idt = 0; idt < devTypeArrayCount; idt++ ){
      if( SUCCEEDED( m_pD3D->GetDeviceCaps( adapterOrdinal, devTypeArray[idt], &Caps))){
        cout<<"Device Type "<<devTypeToString(devTypeArray[idt])<<endl;
      }
    }
  }
  m_pD3D->Release();
  m_pD3D=0;
  return 0;
}