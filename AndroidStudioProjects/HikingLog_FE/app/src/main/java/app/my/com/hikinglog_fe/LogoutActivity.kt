package app.my.com.hikinglog_fe

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class LogoutActivity : AppCompatActivity() {
    // 로그아웃 버튼 클릭시 호출되는 함수
    fun btn_logout(v: View) {
        AlertDialog.Builder(this) // 로그아웃 확인 대화 상자 생성
            .setTitle("로그아웃")
            .setMessage("로그아웃 하시겠습니까?")
            .setPositiveButton("로그아웃") { dialog, positivebtn ->
                val intent = Intent(this, LoginActivity::class.java)
                // 인텐트 플래그 설정
                // FLAG_ACTIVITY_CLEAR_TOP: 이동할 액티비티가 이미 스택에 존재하면 그 위의 액티비티를 모두 종료하고 그 액티비티를 최상위로 가져옴
                // FLAG_ACTIVITY_SINGLE_TOP: 최상위에 이미 해당 액티비티가 있는 경우 새 인텐트를 사용하여 해당 액티비티를 재사용
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                startActivity(intent)
            }
            .setNegativeButton("취소") { dialog, negativebtn ->
                // Do nothing on cancel
            }
            .show()
    }
}