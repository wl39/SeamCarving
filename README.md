# SeamCarving
- 사용 언어: Java
- Java를 이용해 만든 간단한 Seam Carving 프로그램입니다.
	- 유저가 Carve 하고 싶은 이미지와 픽셀 수, Carve 방향을  입력하게 되면, 해당 방향에 맞춰 (가로: width, 세로: height) 이미지에서 상대적으로 불필요한 부분을 제거합니다.
- 개발기간: 14일
- 마지막 수정일: 2018년 3월
  
## 사용방법
- java FileChoose
- 프로그램이 실행 되면 사용자는 3개의 매개변수를 입력할 수 있습니다.
	1. The number of seams to remove: (integer - Textfield)
	2. Horizontal/Vertical Seam: (Boolean - Button)
	3. File for seam carving: (File - Button)
	- *파일을 선택하는 부분을 가장 마지막으로 선택해야 프로그램이 정상적으로 작동합니다.*

---
## 프로그램에서 주목해서 봐야할 점
- Dijkstra algorithm을 통한 Seam carving 구현
