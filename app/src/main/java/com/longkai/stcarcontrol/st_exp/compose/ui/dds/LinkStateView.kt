package com.longkai.stcarcontrol.st_exp.compose.ui.dds

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.layoutId
import com.longkai.stcarcontrol.st_exp.R
import com.longkai.stcarcontrol.st_exp.compose.ui.components.CorneredContainer
import com.longkai.stcarcontrol.st_exp.compose.ui.theme.STCarTheme

@Composable
fun LinkStateView(
    modifier: Modifier,
    uiState: DdsUiState,
) {
    CorneredContainer(
        modifier = modifier,
        cornerSize = 24.dp,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            var linkPageType by remember {
                mutableStateOf(LinkType.DEMO_CAR)
            }
            Box(
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.mipmap.ic_zcu_car1),
                    contentDescription = null,
                )
                if (linkPageType == LinkType.C11) {
                    LinkMapC11View(
                        modifier = Modifier
                            .size(400.dp, 200.dp),
                        uiState = uiState,
                    )
                } else {
                    LinkMapDemoCarView(
                        modifier = Modifier
                            .size(400.dp, 200.dp),
                        uiState = uiState,
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .padding(end = 12.dp)
                    .align(Alignment.End)
            ) {
                Button(
                    onClick = {
                        linkPageType = LinkType.DEMO_CAR
                    }) {
                    Text(
                        text = "Demo Car",
                        color = if (linkPageType == LinkType.DEMO_CAR) {
                            MaterialTheme.colors.onBackground
                        } else {
                            MaterialTheme.colors.background
                        }
                    )
                }
                Spacer(
                    modifier = Modifier.width(8.dp)
                )
                Button(
                    onClick = {
                        linkPageType = LinkType.C11
                    }) {
                    Text(
                        text = "C11",
                        color = if (linkPageType == LinkType.C11) {
                            MaterialTheme.colors.onBackground
                        } else {
                            MaterialTheme.colors.background
                        }
                    )
                }
            }

        }
    }
}

@Composable
private fun LinkMapC11View(
    modifier: Modifier,
    uiState: DdsUiState,
) {
    BoxWithConstraints {
        val constraintSet = ConstraintSet {
            val imageFR = createRefFor("zcu_fr")
            val imageFL = createRefFor("zcu_fl")
            val imageR = createRefFor("zcu_r")
            val imageSGP = createRefFor("zcu_sgp")
//            val num1 = createRefFor("num_1")
//            val num2 = createRefFor("num_2")
//            val num3 = createRefFor("num_3")
//            val num4 = createRefFor("num_4")
            val line2Good = createRefFor("line2_good")
            val line2Error = createRefFor("line2_error")
            val line4Good = createRefFor("line4_good")
            val line4Error = createRefFor("line4_error")
            val line1Good = createRefFor("line1_good")
            val line1Error = createRefFor("line1_error")
            val line3Good = createRefFor("line3_good")
            val line3Error = createRefFor("line3_error")
            constrain(imageFR) {
                top.linkTo(parent.top)
            }
            constrain(imageFL) {
                bottom.linkTo(parent.bottom)
            }
            constrain(imageR) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }
            constrain(imageSGP) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(
                    anchor = imageFL.start,
                    margin = 50.dp,
                )
            }
//            constrain(num1) {
//                top.linkTo(imageFR.bottom)
//                bottom.linkTo(imageSGP.top)
//                start.linkTo(parent.start, (-20).dp)
//            }
//
//            constrain(num3) {
//                top.linkTo(imageSGP.bottom)
//                bottom.linkTo(imageFL.top)
//                start.linkTo(parent.start, (-20).dp)
//            }
//
//            constrain(num2) {
//                top.linkTo(parent.top, (-20).dp)
//                start.linkTo(imageFR.end, (100).dp)
//
//            }
//
//            constrain(num4) {
//                bottom.linkTo(parent.bottom, (-20).dp)
//                start.linkTo(imageFL.end, (100).dp)
//            }

            constrain(line2Good) {
                top.linkTo(imageFR.top, 20.dp)
                start.linkTo(imageFR.end)
            }
            constrain(line2Error) {
                top.linkTo(imageFR.top)
                start.linkTo(imageFR.end)
            }
            constrain(line4Good) {
                bottom.linkTo(imageFL.bottom, 20.dp)
                start.linkTo(imageFL.end)
            }
            constrain(line4Error) {
                bottom.linkTo(imageFL.bottom)
                start.linkTo(imageFL.end)
            }
            constrain(line1Good) {
                end.linkTo(imageSGP.start)
                bottom.linkTo(imageSGP.top, (-15).dp)
            }
            constrain(line1Error) {
                end.linkTo(imageSGP.start)
                bottom.linkTo(imageSGP.top, (-15).dp)
            }
            constrain(line3Good) {
                end.linkTo(imageSGP.start)
                top.linkTo(imageSGP.bottom, (-15).dp)
            }
            constrain(line3Error) {
                end.linkTo(imageSGP.start)
                top.linkTo(imageSGP.bottom, (-15).dp)
            }
        }

        ConstraintLayout(
            constraintSet = constraintSet,
            modifier = modifier,
        ) {
            Image(
                modifier = Modifier
                    .height(60.dp)
                    .width(290.dp)
                    .alpha(if (uiState.link2Status) 100f else 0f)
                    .layoutId("line2_good"),
                painter = painterResource(
                    id = R.mipmap.ic_zcu_c11_line_2_good
                ),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
            )
            Image(
                modifier = Modifier
                    .height(80.dp)
                    .width(290.dp)
                    .alpha(if (!uiState.link2Status) 100f else 0f)
                    .layoutId("line2_error"),
                painter = painterResource(
                    id = R.mipmap.ic_zcu_c11_line_2_error
                ),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
            )

            Image(
                modifier = Modifier
                    .height(60.dp)
                    .width(290.dp)
                    .alpha(if (uiState.link4Status) 100f else 0f)
                    .layoutId("line4_good"),
                painter = painterResource(
                    id = R.mipmap.ic_zcu_c11_line_4_good
                ),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
            )
            Image(
                modifier = Modifier
                    .alpha(if (!uiState.link4Status) 100f else 0f)
                    .height(80.dp)
                    .width(290.dp)
                    .layoutId("line4_error"),
                painter = painterResource(
                    id = R.mipmap.ic_zcu_c11_line_4_error
                ),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
            )
            Image(
                modifier = Modifier
                    .height(55.dp)
                    .width(25.dp)
                    .alpha(if (uiState.link1Status) 100f else 0f)
                    .layoutId("line1_good"),
                painter = painterResource(
                    id = R.mipmap.ic_zcu_c11_line_1_good
                ),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
            )
            Image(
                modifier = Modifier
                    .height(55.dp)
                    .width(40.dp)
                    .alpha(if (!uiState.link1Status) 100f else 0f)
                    .layoutId("line1_error"),
                painter = painterResource(
                    id = R.mipmap.ic_zcu_c11_line_1_error
                ),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
            )

            Image(
                modifier = Modifier
                    .height(55.dp)
                    .width(25.dp)
                    .alpha(if (uiState.link3Status) 100f else 0f)
                    .layoutId("line3_good"),
                painter = painterResource(
                    id = R.mipmap.ic_zcu_c11_line_3_good
                ),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
            )
            Image(
                modifier = Modifier
                    .height(55.dp)
                    .width(40.dp)
                    .alpha(if (!uiState.link3Status) 100f else 0f)
                    .layoutId("line3_error"),
                painter = painterResource(
                    id = R.mipmap.ic_zcu_c11_line_3_error
                ),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
            )

            Image(
                modifier = Modifier
                    .height(40.dp)
                    .layoutId("zcu_fr"),
                painter = painterResource(id = R.mipmap.ic_zcu_fr),
                contentDescription = "",
            )
            Image(
                modifier = Modifier
                    .height(40.dp)
                    .layoutId("zcu_fl"),
                painter = painterResource(id = R.mipmap.ic_zcu_fl),
                contentDescription = "",
            )
            Image(
                modifier = Modifier
                    .height(40.dp)
                    .layoutId("zcu_r"),
                painter = painterResource(id = R.mipmap.ic_zcu_r),
                contentDescription = "",
            )
            Image(
                modifier = Modifier
                    .height(40.dp)
                    .layoutId("zcu_sgp"),
                painter = painterResource(id = R.mipmap.ic_zcu_sgp),
                contentDescription = "",
            )
//            Image(
//                modifier = Modifier
//                    .layoutId("num_1")
//                    .height(30.dp),
//                painter = painterResource(id = R.mipmap.ic_zcu_line_num_1),
//                contentDescription = "",
//            )
//            Image(
//                modifier = Modifier
//                    .layoutId("num_2")
//                    .height(30.dp),
//                painter = painterResource(id = R.mipmap.ic_zcu_line_num_2),
//                contentDescription = "",
//            )
//            Image(
//                modifier = Modifier
//                    .layoutId("num_3")
//                    .height(30.dp),
//                painter = painterResource(id = R.mipmap.ic_zcu_line_num_3),
//                contentDescription = "",
//            )
//            Image(
//                modifier = Modifier
//                    .layoutId("num_4")
//                    .height(30.dp),
//                painter = painterResource(id = R.mipmap.ic_zcu_line_num_4),
//                contentDescription = "",
//            )


        }
    }
}

@Composable
private fun LinkMapDemoCarView(
    modifier: Modifier,
    uiState: DdsUiState,
) {
    BoxWithConstraints {
        val constraintSet = ConstraintSet {
            val imageR = createRefFor("zcu_r")
            val imageF = createRefFor("zcu_f")
            val imageSGP = createRefFor("zcu_sgp")

            val line1Good = createRefFor("line1_good")
            val line1Error = createRefFor("line1_error")
            val line3Good = createRefFor("line3_good")
            val line3Error = createRefFor("line3_error")
            val line2Good = createRefFor("line2_good")
            val line2Error = createRefFor("line2_error")

            constrain(imageR) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
            }

            constrain(imageF) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            }

            constrain(imageSGP) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }

            constrain(line2Good) {
                start.linkTo(imageF.start)
                end.linkTo(imageR.end)
                bottom.linkTo(imageF.top)
            }
            constrain(line2Error) {
                start.linkTo(imageF.start)
                end.linkTo(imageR.end)
                bottom.linkTo(imageF.top)
            }

            constrain(line1Good) {
                end.linkTo(imageSGP.start)
                bottom.linkTo(imageSGP.bottom, 20.dp)
            }
            constrain(line1Error) {
                end.linkTo(imageSGP.start)
                bottom.linkTo(imageSGP.bottom, 5.dp)
            }

            constrain(line3Good) {
                start.linkTo(imageSGP.end)
                bottom.linkTo(imageSGP.bottom, 20.dp)
            }
            constrain(line3Error) {
                start.linkTo(imageSGP.end)
                bottom.linkTo(imageSGP.bottom, 5.dp)
            }
        }
        ConstraintLayout(
            constraintSet = constraintSet,
            modifier = modifier,
        ) {
            Image(
                modifier = Modifier
                    .height(60.dp)
                    .width(335.dp)
                    .alpha(if (uiState.link2Status) 100f else 0f)
                    .layoutId("line2_good"),
                painter = painterResource(
                    id = R.mipmap.ic_zcu_dc_line_2_good
                ),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
            )
            Image(
                modifier = Modifier
                    .height(80.dp)
                    .width(335.dp)
                    .alpha(if (!uiState.link2Status) 100f else 0f)
                    .layoutId("line2_error"),
                painter = painterResource(
                    id = R.mipmap.ic_zcu_dc_line_2_error
                ),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
            )

            Image(
                modifier = Modifier
                    .height(60.dp)
                    .width(140.dp)
                    .alpha(if (uiState.link3Status) 100f else 0f)
                    .layoutId("line3_good"),
                painter = painterResource(
                    id = R.mipmap.ic_zcu_dc_line_3_good
                ),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
            )
            Image(
                modifier = Modifier
                    .height(80.dp)
                    .width(140.dp)
                    .alpha(if (!uiState.link3Status) 100f else 0f)
                    .layoutId("line3_error"),
                painter = painterResource(
                    id = R.mipmap.ic_zcu_dc_line_3_error
                ),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
            )
            Image(
                modifier = Modifier
                    .height(60.dp)
                    .width(140.dp)
                    .alpha(if (uiState.link1Status) 100f else 0f)
                    .layoutId("line1_good"),
                painter = painterResource(
                    id = R.mipmap.ic_zcu_dc_line_1_good
                ),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
            )
            Image(
                modifier = Modifier
                    .height(80.dp)
                    .width(140.dp)
                    .alpha(if (!uiState.link1Status) 100f else 0f)
                    .layoutId("line1_error"),
                painter = painterResource(
                    id = R.mipmap.ic_zcu_dc_line_1_error
                ),
                contentDescription = "",
                contentScale = ContentScale.FillBounds,
            )

            Image(
                modifier = Modifier
                    .height(40.dp)
                    .layoutId("zcu_f"),
                painter = painterResource(id = R.mipmap.ic_zcu_f),
                contentDescription = "",
            )
            Image(
                modifier = Modifier
                    .height(40.dp)
                    .layoutId("zcu_r"),
                painter = painterResource(id = R.mipmap.ic_zcu_r),
                contentDescription = "",
            )
            Image(
                modifier = Modifier
                    .height(40.dp)
                    .layoutId("zcu_sgp"),
                painter = painterResource(id = R.mipmap.ic_zcu_sgp),
                contentDescription = "",
            )
        }
    }
}

enum class LinkType {
    C11,
    DEMO_CAR,
    ;
}

@Composable
@Preview
private fun LinkStateViewPreview() {
    STCarTheme {
        LinkStateView(
            modifier = Modifier,
            uiState = DdsUiState()
        )
    }
}