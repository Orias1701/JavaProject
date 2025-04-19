#include <stdio.h>

// Dat phong 
class loaiphong{
	string maloai;
	int gialoai 
}; 
class khachhang {
	string makhachhang, tinhtrangkhach;
	arr tinhtrangkhach = {'dang o', 'da roi'};
};

class phong {
	string maphong,maloai; 
	arr ttphong = {'trong', 'dang su dung', 'da dat'};
};

class datphong {
	string maphong, makhachhang;
	int nhanphong, traphong,tienphat;
	string cachdat;
	string ttdat; 
};
void tienphong(int tienphong){
	if(loaiphong.gialoai=phong.gialoai){
		tienphong=loaiphong.gialoai; 
	} 
	return tienphong; 
} 
bool quahan(int thuc, int nhanphong, int traphong, string cachdat) {
	if (thuc > traphong + 30p) {
		return true;
	}
	if (cachdat == "dat online" && thuc > nhanphong + 15p) {
		return true;
	}
	return false;
}

bool dangdoi(string cachdat) {
	if (cachdat == "dat online") {
		return true;
	}
	return false;
}
int tinhTienPhat(int thuc, int traphong, string ttdat, string ttphong, int tienphong) {
	int tienphat;
	if (ttdat == "qua han" && ttphong == "dang su dung") {
		if (thuc > traphong + 30p) {
			int thoigianvuot = thuc - (traphong + 30p);
			tienphat = tienphong * 0.3 * (thoigianvuot / 120);
		}
	}
	else{
		tienphat=0 
	} 
	return tienphat;
}

bool hople(int nhanphong, int traphong) {
	if (traphong > nhanphong) {
		return true;
	}
	return false;
}

// MAIN
int main {
	//ttdat:Dang su dung,Qua han,Dang doi,Da tra 
	//ttphong:Trong,Dang su dung,Da dat
	//cachdat:online,tructiep 
//	int nhanphong, traphong, thuc;
//	string ttdat, ttphong, cachdat, maphong, makhachhang;
//	int tienphong;
//	int tienphat;
	int ao;
	ao = nhanphong - 12h;
	tienphat = tinhTienPhat(thuc, traphong, ttdat, ttphong, tienphong);
	tienphong = tienphong + tienphat;
	if (ttdat == "dang su dung") {
		ttphong = "dang su dung";
		tinhtrangkhach = "dang o";
	}
	else if (ttdat == "qua han" && quahan(thuc, nhanphong, traphong, cachdat)) {
		if (cachdat == "dat truc tiep") {
			ttphong = "dang su dung";
			tinhtrangkhach = "dang o";
			tienphong = tienphong + tinhTienPhat(thuc, traphong, ttdat, ttphong, tienphong);
		} else {
			ttphong = "trong";
			tinhtrangkhach = "da roi";
		}
	}
	else if (ttdat == "dang doi" && dangdoi(cachdat)) {
		if (thuc < ao) {
			ttphong = "trong";
			tinhtrangkhach = "da roi";
		} else if (thuc > ao) {
			ttphong = "dang doi";
			tinhtrangkhach = "dang o";
		}
	}
	else if (ttdat == "da tra") {
		ttphong = "trong";
		tinhtrangkhach = "da roi";
	}
}

