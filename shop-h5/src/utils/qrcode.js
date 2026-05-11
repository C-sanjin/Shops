import QRCode from 'qrcode'

export function generateQRCode(text) {
  return new Promise((resolve, reject) => {
    QRCode.toDataURL(text, {
      width: 200,
      margin: 2,
      color: {
        dark: '#000000',
        light: '#ffffff'
      }
    }, (error, url) => {
      if (error) {
        reject(error)
      } else {
        resolve(url)
      }
    })
  })
}
