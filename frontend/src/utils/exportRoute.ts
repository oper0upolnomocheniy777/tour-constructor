import { RoutePoint } from '../types';

// Экспорт в JSON
export const exportToJSON = (points: RoutePoint[], title: string) => {
  const data = {
    title: title,
    exportedAt: new Date().toISOString(),
    pointsCount: points.length,
    points: points.map(p => ({
      name: p.name,
      description: p.description,
      latitude: p.latitude,
      longitude: p.longitude,
      order: p.order
    }))
  };
  
  const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' });
  const url = URL.createObjectURL(blob);
  downloadFile(url, `${title.replace(/[^a-zа-яё0-9]/gi, '_')}.json`);
  URL.revokeObjectURL(url);
};

// Экспорт в GPX (формат GPS-треков)
export const exportToGPX = (points: RoutePoint[], title: string) => {
  const sanitizedTitle = title.replace(/[^a-zа-яё0-9]/gi, '_');
  
  const gpxHeader = `<?xml version="1.0" encoding="UTF-8"?>
<gpx version="1.1" creator="Tour Constructor" xmlns="http://www.topografix.com/GPX/1/1">
  <metadata>
    <name>${title}</name>
    <time>${new Date().toISOString()}</time>
  </metadata>
  <trk>
    <name>${title}</name>
    <trkseg>`;
    
  const gpxPoints = points.map(p => `
      <trkpt lat="${p.latitude}" lon="${p.longitude}">
        <name>${p.name}</name>
        <desc>${p.description || ''}</desc>
      </trkpt>`).join('');
    
  const gpxFooter = `
    </trkseg>
  </trk>
</gpx>`;
  
  const gpxContent = gpxHeader + gpxPoints + gpxFooter;
  const blob = new Blob([gpxContent], { type: 'application/gpx+xml' });
  const url = URL.createObjectURL(blob);
  downloadFile(url, `${sanitizedTitle}.gpx`);
  URL.revokeObjectURL(url);
};

// Вспомогательная функция скачивания
const downloadFile = (url: string, filename: string) => {
  const a = document.createElement('a');
  a.href = url;
  a.download = filename;
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(a);
};