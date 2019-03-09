package adam.android.project;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuBuilder;
import android.text.BoringLayout;
import android.text.Layout;
import android.text.StaticLayout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.view.ViewGroup.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sac.game.AlphaBetaPruning;
import sac.game.GameSearchAlgorithm;
import sac.game.GameSearchConfigurator;
import sac.game.GameStateImpl;
import sac.graph.GraphSearchConfigurator;
import sac.graphviz.GameSearchGraphvizer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TableLayout mainBoardLayout;
    private ConstraintLayout con;
    private Display display;
    private Thread gameThread = null;

    int screenWidth;
    int screenHeight;

    Drawable whiteOnSquareImg;
    Drawable blackOnSquareImg;
    Drawable whiteDameOnSquareImg;
    Drawable blackDameOnSquareImg;
    Drawable whiteOnSquareImgFocused;
    Drawable blackOnSquareImgFocused;
    Drawable whiteDameOnSquareImgFocused;
    Drawable blackDameOnSquareImgFocused;
    Drawable darkSquareInRedFrame;

    TextView whiteAmountText;
    TextView blackAmountText;
    TextView winningText;
    Button newGameButton;

    private int currentPieceId = 0;
    private int currentPieceX = 0;
    private int currentPieceY = 0;

    GameSearchAlgorithm alg;

    Menu menu;

    Checkers checkers = new Checkers();

    List<ImageButton> imgList = new ArrayList<ImageButton>();

    public Bitmap createSingleImageFromMultipleImages(Bitmap firstImage, Bitmap secondImage, int xShift, int yShift) {
        Bitmap result = Bitmap.createBitmap(firstImage.getWidth(), firstImage.getHeight(), firstImage.getConfig());
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(firstImage, 0f, 0f, null);
        canvas.drawBitmap(secondImage, xShift, yShift, null);
        return result;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainBoardLayout = (TableLayout) findViewById(R.id.boardLayout);
        con = (ConstraintLayout) findViewById(R.id.con);
        con.setBackgroundResource(R.drawable.board);

        display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x - (size.x%8);
        screenHeight = size.y - (size.y%8);

        showSettingsWindow();

        Bitmap darkSquareImg = BitmapFactory.decodeResource(getResources(), R.drawable.dark_square);
        Bitmap whitePieceImg = BitmapFactory.decodeResource(getResources(), R.drawable.white_piece2);
        Bitmap blackPieceImg = BitmapFactory.decodeResource(getResources(), R.drawable.black_piece2);
        Bitmap whiteDameImg = BitmapFactory.decodeResource(getResources(), R.drawable.white_dame);
        Bitmap blackDameImg = BitmapFactory.decodeResource(getResources(), R.drawable.black_dame);
        Bitmap redFrame = BitmapFactory.decodeResource(getResources(), R.drawable.red_frame);
        darkSquareInRedFrame = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, redFrame, 0, 0));
        whiteOnSquareImg = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, whitePieceImg, 25, 25));
        blackOnSquareImg = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, blackPieceImg, 25, 25));
        whiteDameOnSquareImg = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, whiteDameImg, 25, 25));
        blackDameOnSquareImg = new BitmapDrawable(getResources(), createSingleImageFromMultipleImages(darkSquareImg, blackDameImg, 25, 25));

        whiteOnSquareImgFocused = whiteOnSquareImg.getConstantState().newDrawable().mutate();
        whiteOnSquareImgFocused.setColorFilter(Color.WHITE, PorterDuff.Mode.OVERLAY);
        blackOnSquareImgFocused = blackOnSquareImg.getConstantState().newDrawable().mutate();
        blackOnSquareImgFocused.setColorFilter(Color.BLACK, PorterDuff.Mode.OVERLAY);
        whiteDameOnSquareImgFocused = whiteDameOnSquareImg.getConstantState().newDrawable().mutate();
        whiteDameOnSquareImgFocused.setColorFilter(Color.WHITE, PorterDuff.Mode.OVERLAY);
        blackDameOnSquareImgFocused = blackDameOnSquareImg.getConstantState().newDrawable().mutate();
        blackDameOnSquareImgFocused.setColorFilter(Color.BLACK, PorterDuff.Mode.OVERLAY);
    }

    public void startGame() {
        for (int i = 0; i < 8; i++)
        {
            TableRow tableRow = new TableRow(this);
            TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT);
            tableRow.setLayoutParams(lp);
            for (int j = 0; j < 8; j++)
            {
                ImageButton imgButton = new ImageButton(this);
                imgButton.setLayoutParams(new TableRow.LayoutParams(screenWidth / 8, screenWidth / 8));
                imgButton.setId(i*8+j);

                if (checkers.board[i][j] == -1) {
                    imgButton.setBackgroundResource(R.drawable.white_square);
                }
                else if (checkers.board[i][j] == 0) {
                    imgButton.setBackgroundResource(R.drawable.dark_square);
                }
                else if (checkers.board[i][j] == 1) {
                    imgButton.setBackground(whiteOnSquareImg);
                }
                else
                {
                    imgButton.setBackground(blackOnSquareImg);
                }

                tableRow.addView(imgButton);
                imgList.add(imgButton);
            }

            mainBoardLayout.addView(tableRow, i);
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (checkers.board[i][j] != -1) {
                    imgList.get(i * 8 + j).setOnClickListener(this);
                }
            }
        }

        whiteAmountText = new TextView(this);
        blackAmountText = new TextView(this);
        winningText = new TextView(this);
        winningText.setAllCaps(true);
        winningText.setTextSize(25);
        /*newGameButton = new Button(this);
        newGameButton.setText("New game");
        newGameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkers.newGame();
                drawBoard();
            }
        });*/

        mainBoardLayout.addView(whiteAmountText);
        mainBoardLayout.addView(blackAmountText);
        mainBoardLayout.addView(winningText);

        GameSearchConfigurator conf = new GameSearchConfigurator();
        //conf.setParentsMemorizingChildren(true);
        conf.setDepthLimit(2);
        //conf.setRefutationTableOn(false);
        //conf.setTranspositionTableOn(false);

        alg = new AlphaBetaPruning(checkers, conf);

        Checkers.setHFunction(new Heuristics());
        checkers.setMaximizingTurnNow(true);

        run();
        drawBoard();
    }

    public void showSettingsWindow() {
        TextView textView = new TextView(this);
        textView.setText("Choose difficulty");
        textView.setTextSize(38);
        textView.setTextColor(Color.WHITE);

        RadioButton easyRadio = new RadioButton(this);
        easyRadio.setText("Easy");
        easyRadio.setTextSize(38);
        easyRadio.setTextColor(Color.WHITE);
        RadioButton mediumRadio = new RadioButton(this);
        mediumRadio.setText("Medium");
        mediumRadio.setTextSize(38);
        mediumRadio.setTextColor(Color.WHITE);
        RadioButton hardRadio = new RadioButton(this);
        hardRadio.setText("Hard");
        hardRadio.setTextSize(38);
        hardRadio.setTextColor(Color.WHITE);
        RadioButton veryHardRadio = new RadioButton(this);
        veryHardRadio.setText("Very hard");
        veryHardRadio.setTextSize(38);
        veryHardRadio.setTextColor(Color.WHITE);
        veryHardRadio.setDrawingCacheBackgroundColor(Color.WHITE);

        RadioGroup radioGroup = new RadioGroup(this);
        radioGroup.addView(easyRadio);
        radioGroup.addView(mediumRadio);
        radioGroup.addView(hardRadio);
        radioGroup.addView(veryHardRadio);

        mainBoardLayout.addView(textView, 0);
        mainBoardLayout.addView(radioGroup ,1);

        mainBoardLayout.setPadding(screenWidth / 10, 0, screenWidth / 10, 0);

        newGameButton = new Button(this);
        newGameButton.setText("New game");
        newGameButton.setWidth(1000);
        newGameButton.setHeight(200);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startGame();
                textView.setVisibility(View.INVISIBLE);
                radioGroup.setVisibility(View.INVISIBLE);
                con.setBackgroundColor(Color.DKGRAY);
                mainBoardLayout.setPadding(0, screenHeight / 8, 0, 0);
            }
        });
        mainBoardLayout.addView(newGameButton);
    }

    public void run() {
        alg.execute();
        //System.out.println("BEST SCORES:        " + alg.getMovesScores());
        //GameSearchGraphvizer.go(alg , "D:/crap/files/output.dot" , true , true);
        //System.out.println("DEPTH:         " + alg.getDepthReached());
        String str = alg.getFirstBestMove();
        System.out.println("CLOSED: " + alg.getClosedStatesCount());
        System.out.println("TIME: " + alg.getDurationTime());
        //System.out.println("BEST MOVE:      " + alg.getFirstBestMove());
        if (str != null) {
            checkers.checkMove(Character.getNumericValue(str.charAt(0)), Character.getNumericValue(str.charAt(1)), Character.getNumericValue(str.charAt(2)), Character.getNumericValue(str.charAt(3)));
        }
    }

    void drawBoard() {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (checkers.board[i][j] == -1) {
                    imgList.get(i*8+j).setBackgroundResource(R.drawable.white_square);
                }
                else if (checkers.board[i][j] == 0) {
                    imgList.get(i*8+j).setBackgroundResource(R.drawable.dark_square);
                }
                else if (checkers.board[i][j] == 1) {
                    imgList.get(i*8+j).setBackground(whiteOnSquareImg);
                }
                else if (checkers.board[i][j] == 2)
                {
                    imgList.get(i*8+j).setBackground(blackOnSquareImg);
                }
                else if (checkers.board[i][j] == 3)
                {
                    imgList.get(i*8+j).setBackground(whiteDameOnSquareImg);
                }
                else if (checkers.board[i][j] == 4)
                {
                    imgList.get(i*8+j).setBackground(blackDameOnSquareImg);
                }
            }
        }
        if (currentPieceId != 0) {
            if (checkers.board[currentPieceX][currentPieceY] == 1)
                imgList.get(currentPieceId).setBackground(whiteOnSquareImgFocused);
            else if (checkers.board[currentPieceX][currentPieceY] == 2)
                imgList.get(currentPieceId).setBackground(blackOnSquareImgFocused);
            else if (checkers.board[currentPieceX][currentPieceY] == 3)
                imgList.get(currentPieceId).setBackground(whiteDameOnSquareImgFocused);
            else if (checkers.board[currentPieceX][currentPieceY] == 4)
                imgList.get(currentPieceId).setBackground(blackDameOnSquareImgFocused);
        }

        try {
            checkers.checkBoard();
            for (int k = 0; k < checkers.bestCaptures.size(); ++k) {
                if ((currentPieceId/8) == Character.getNumericValue(checkers.bestCaptures.get(k).charAt(0)) && (currentPieceId%8) == Character.getNumericValue(checkers.bestCaptures.get(k).charAt(1))) {
                    int x = Character.getNumericValue(checkers.bestCaptures.get(k).charAt(checkers.bestCaptures.get(k).length()-2));
                    int y = Character.getNumericValue(checkers.bestCaptures.get(k).charAt(checkers.bestCaptures.get(k).length()-1));
                    imgList.get(x*8+y).setBackground(darkSquareInRedFrame);
                }
            }
        }
        catch (Exception e){ }

        if (checkers.isEnd()) {
            AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
            dlgAlert.setMessage(checkers.winningCommunicate);
            dlgAlert.setTitle("Checkers");
            dlgAlert.setPositiveButton("OK", null);
            dlgAlert.setCancelable(true);
            dlgAlert.create().show();


            //mainBoardLayout.removeAllViews();
            //con.setBackgroundResource(R.drawable.board);
            checkers.newGame();
            drawBoard();
            //showSettingsWindow();
        }
    }

    @Override
    public void onClick(View v) {
        System.out.println("Click!");
        if (currentPieceId == 0 || checkers.board[((int) v.getId()) / 8][((int) v.getId()) % 8] != 0) {
            try {
                for (int i = 0; i < checkers.bestCaptures.size(); ++i) {
                    if ((((int) v.getId()) / 8 == Character.getNumericValue(checkers.bestCaptures.get(i).charAt(checkers.bestCaptures.get(i).length() - 2))) && (((int) v.getId()) % 8 == Character.getNumericValue(checkers.bestCaptures.get(i).charAt(checkers.bestCaptures.get(i).length() - 1)))) {
                        checkers.checkMove(currentPieceX, currentPieceY, ((int) v.getId()) / 8, ((int) v.getId()) % 8);
                    }
                }
            } catch (NullPointerException ex) {}
            currentPieceId = v.getId();
            currentPieceX = currentPieceId / 8;
            currentPieceY = currentPieceId % 8;
        }
        else {
            checkers.checkMove(currentPieceX, currentPieceY, ((int) v.getId()) / 8, ((int) v.getId()) % 8);
            currentPieceId = 0;
            currentPieceX = 0;
            currentPieceY = 0;
        }

        if (checkers.isMaximizingTurnNow()) {
            run();
        }

        drawBoard();
    }
}
