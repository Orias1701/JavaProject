#include <stdio.h>
//ÐAT PHONG 
class phong{
	string maphong 
}; 
class datphong{
	string maphong 
}; 
void tienphat{
	if(ttdat='Qua han'&&ttphong='Dang su dung'){
		if (thuc > traphong + 30p) {
		    thoigianvuot = thuc - (traphong + 30p);
		    tienphat = tienphong * 0.3 * (thoigianvuot / 120); 
		}
	}
	else{
		tienphat=0; 
	} 
	return tienphat; 
} 
bool trong{
	for(int i=0;i<phong.size;i++){
		if(datphong.maphong!=phong[i].maphong){
			return true; 
		}
		else if (datphong.maphong=phong.maphong&&ttdat='datra'){
			return true; 
		} 
	} 
} 
bool quahan{
	if (thuc>traphong+30p){
		return true; 
	} 
	if (thuc>nhanphong+15p&&cachdat='online'){
		return true 
	} 
} 
bool dangdoi{
	if(cachdat='online'){
		return true; 
	} 
} 
bool hople{
	if(traphong>nhanphong){
		return true; 
	} 
} 
int main{
	//ttdat:Dang su dung,Qua han,Dang doi,Da tra 
	//ttphong:Trong,Dang su dung,Da dat
	//cachdat:online,tructiep 
	int traphong,nhanphong,thuc,ao;
	String ttdat,ttphong,cachdat,maphong;
	int tienphong;
	tienphong=tienphat+tienphong; 
	ao=nhanphong-12h; 
	if(ttdat='Dang su dung'){
		ttphong='Dang su dung';
	} 
	else if(ttdat='Qua han'&&quahan){
		if(cachdat='truc tiep'){
			ttphong='dang su dung'; 
			tienphong=tienphat+tienphong; 
		} 
		else{
			ttphong='trong'; 
		} 
	} 
	else if (ttdat='dang doi'&&dangdoi){
		if (thuc < ao) {
		    ttphong = 'trong';
		} else (thuc>ao) {
		    ttphong = 'dang doi';
		}

	} 
} 
